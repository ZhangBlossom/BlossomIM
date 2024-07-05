package blossom.project.im.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang3.StringUtils;
import blossom.project.base.BaseInfoProperties;
import blossom.project.grace.result.GraceJSONResult;
import blossom.project.pojo.FriendCircle;
import blossom.project.pojo.FriendCircleLiked;
import blossom.project.pojo.bo.FriendCircleBO;
import blossom.project.pojo.vo.CommentVO;
import blossom.project.pojo.vo.FriendCircleVO;
import blossom.project.im.service.CommentService;
import blossom.project.im.service.FriendCircleService;
import blossom.project.utils.PagedGridResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Auther 风间影月
 */
@RestController
@RequestMapping("friendCircle")
public class FriendCircleController extends BaseInfoProperties {

    @Resource
    private FriendCircleService friendCircleService;

    @Resource
    private CommentService commentService;

    @PostMapping("publish")
    public GraceJSONResult publish(@RequestBody FriendCircleBO friendCircleBO,
                                   HttpServletRequest request) {

        String userId = request.getHeader(HEADER_USER_ID);

        friendCircleBO.setUserId(userId);
        friendCircleBO.setPublishTime(LocalDateTime.now());

        friendCircleService.publish(friendCircleBO);

        return GraceJSONResult.ok();
    }

    @PostMapping("queryList")
    public GraceJSONResult publish(String userId,
       @RequestParam(defaultValue = "1", name = "page") Integer page,
       @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize) {

        if (StringUtils.isBlank(userId)) return GraceJSONResult.error();

        PagedGridResult gridResult = friendCircleService.queryList(userId, page, pageSize);

        List<FriendCircleVO> list = (List<FriendCircleVO>)gridResult.getRows();
        for (FriendCircleVO f : list) {
            String friendCircleId = f.getFriendCircleId();
            List<FriendCircleLiked> likedList = friendCircleService.queryLikedFriends(friendCircleId);
            f.setLikedFriends(likedList);

            boolean res = friendCircleService.doILike(friendCircleId, userId);
            f.setDoILike(res);

            List<CommentVO> commentList = commentService.queryAll(friendCircleId);
            f.setCommentList(commentList);
        }

        return GraceJSONResult.ok(gridResult);
    }

    @PostMapping("like")
    public GraceJSONResult like(String friendCircleId,
                                HttpServletRequest request) {

        String userId = request.getHeader(HEADER_USER_ID);
        friendCircleService.like(friendCircleId, userId);

        return GraceJSONResult.ok();
    }

    @PostMapping("unlike")
    public GraceJSONResult unlike(String friendCircleId,
                                  HttpServletRequest request) {

        String userId = request.getHeader(HEADER_USER_ID);
        friendCircleService.unlike(friendCircleId, userId);

        return GraceJSONResult.ok();
    }

    @PostMapping("likedFriends")
    public GraceJSONResult likedFriends(String friendCircleId,
                                  HttpServletRequest request) {
        List<FriendCircleLiked> likedList =
                friendCircleService.queryLikedFriends(friendCircleId);
        return GraceJSONResult.ok(likedList);
    }

    @PostMapping("delete")
    public GraceJSONResult delete(String friendCircleId,
                                  HttpServletRequest request) {

        String userId = request.getHeader(HEADER_USER_ID);
        friendCircleService.delete(friendCircleId, userId);

        return GraceJSONResult.ok();
    }
}

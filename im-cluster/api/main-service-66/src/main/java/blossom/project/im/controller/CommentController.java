package blossom.project.im.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import blossom.project.base.BaseInfoProperties;
import blossom.project.grace.result.GraceJSONResult;
import blossom.project.pojo.FriendCircleLiked;
import blossom.project.pojo.bo.CommentBO;
import blossom.project.pojo.bo.FriendCircleBO;
import blossom.project.pojo.vo.CommentVO;
import blossom.project.pojo.vo.FriendCircleVO;
import blossom.project.im.service.CommentService;
import blossom.project.utils.PagedGridResult;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther 风间影月
 */
@RestController
@RequestMapping("comment")
public class CommentController extends BaseInfoProperties {

    @Resource
    private CommentService commentService;

    @PostMapping("create")
    public GraceJSONResult create(@RequestBody CommentBO friendCircleBO,
                                  HttpServletRequest request) {
        CommentVO commentVO = commentService.createComment(friendCircleBO);
        return GraceJSONResult.ok(commentVO);
    }

    @PostMapping("query")
    public GraceJSONResult create(String friendCircleId) {
        return GraceJSONResult.ok(commentService.queryAll(friendCircleId));
    }

    @PostMapping("delete")
    public GraceJSONResult delete(String commentUserId,
                                  String commentId,
                                  String friendCircleId) {

        if (StringUtils.isBlank(commentUserId) ||
                StringUtils.isBlank(commentId) ||
                StringUtils.isBlank(friendCircleId)
        ) {
            return GraceJSONResult.error();
        }

        commentService.deleteComment(commentUserId, commentId, friendCircleId);
        return GraceJSONResult.ok();
    }

}

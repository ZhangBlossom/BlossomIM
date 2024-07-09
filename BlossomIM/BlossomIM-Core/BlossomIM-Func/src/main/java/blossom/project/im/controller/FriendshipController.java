package blossom.project.im.controller;

import blossom.project.im.Friendship;
import blossom.project.im.base.BaseInfoProperties;
import blossom.project.im.enums.YesOrNo;
import blossom.project.im.grace.result.GraceJSONResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import blossom.project.im.service.FriendshipService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther ZhangBlossom
 */
@RestController
@RequestMapping("friendship")
@Slf4j
public class FriendshipController extends BaseInfoProperties {

    @Resource
    private FriendshipService friendshipService;

    @PostMapping("getFriendship")
    public GraceJSONResult pass(String friendId, HttpServletRequest request) {

        String myId = request.getHeader(HEADER_USER_ID);

        Friendship friendship = friendshipService.getFriendship(myId, friendId);
        return GraceJSONResult.ok(friendship);
    }

    @PostMapping("queryMyFriends")
    public GraceJSONResult queryMyFriends(HttpServletRequest request) {
        String myId = request.getHeader(HEADER_USER_ID);
        return GraceJSONResult.ok(friendshipService.queryMyFriends(myId, false));
    }

    @PostMapping("queryMyBlackList")
    public GraceJSONResult queryMyBlackList(HttpServletRequest request) {
        String myId = request.getHeader(HEADER_USER_ID);
        return GraceJSONResult.ok(friendshipService.queryMyFriends(myId, true));
    }

    @PostMapping("updateFriendRemark")
    public GraceJSONResult updateFriendRemark(HttpServletRequest request,
                                              String friendId,
                                              String friendRemark) {

        if (StringUtils.isBlank(friendId) || StringUtils.isBlank(friendRemark)) {
            return GraceJSONResult.error();
        }

        String myId = request.getHeader(HEADER_USER_ID);
        friendshipService.updateFriendRemark(myId, friendId, friendRemark);
        return GraceJSONResult.ok();
    }

    @PostMapping("tobeBlack")
    public GraceJSONResult tobeBlack(HttpServletRequest request,
                                     String friendId) {

        if (StringUtils.isBlank(friendId)) {
            return GraceJSONResult.error();
        }

        String myId = request.getHeader(HEADER_USER_ID);
        friendshipService.updateBlackList(myId, friendId, YesOrNo.YES);
        return GraceJSONResult.ok();
    }

    @PostMapping("moveOutBlack")
    public GraceJSONResult moveOutBlack(HttpServletRequest request,
                                     String friendId) {

        if (StringUtils.isBlank(friendId)) {
            return GraceJSONResult.error();
        }

        String myId = request.getHeader(HEADER_USER_ID);
        friendshipService.updateBlackList(myId, friendId, YesOrNo.NO);
        return GraceJSONResult.ok();
    }

    @PostMapping("delete")
    public GraceJSONResult delete(HttpServletRequest request,
                                        String friendId) {

        if (StringUtils.isBlank(friendId)) {
            return GraceJSONResult.error();
        }

        String myId = request.getHeader(HEADER_USER_ID);
        friendshipService.delete(myId, friendId);
        return GraceJSONResult.ok();
    }

    /**
     * 判断两个朋友之前的关系是否拉黑
     * @param friendId1st
     * @param friendId2nd
     * @return
     */
    @GetMapping("isBlack")
    public GraceJSONResult isBlack(String friendId1st, String friendId2nd) {

        // 需要进行两次查询，A拉黑B，B拉黑A，AB相互拉黑
        // 只需要符合其中的一个条件，就表示双发发送消息不可送达
        return GraceJSONResult.ok(
                friendshipService.isBlackEachOther(
                        friendId1st, friendId2nd));
    }
}

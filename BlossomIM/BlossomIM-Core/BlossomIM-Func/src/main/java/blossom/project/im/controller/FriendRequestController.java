package blossom.project.im.controller;

import blossom.project.im.base.BaseInfoProperties;
import blossom.project.im.bo.NewFriendRequestBO;
import blossom.project.im.grace.result.GraceJSONResult;
import blossom.project.im.service.FriendRequestService;
import blossom.project.im.utils.PagedGridResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

/**
 * @Auther ZhangBlossom
 */
@RestController
@RequestMapping("friendRequest")
@Slf4j
public class FriendRequestController extends BaseInfoProperties {

    @Resource
    private FriendRequestService friendRequestService;

    @PostMapping("add")
    public GraceJSONResult add(@RequestBody @Valid NewFriendRequestBO friendRequestBO) {
        friendRequestService.addNewRequest(friendRequestBO);
        return GraceJSONResult.ok();
    }

    // userId=" + userId + "&page=" + page + "&pageSize=15
    @PostMapping("queryNew")
    public GraceJSONResult queryNew(HttpServletRequest request,
        @RequestParam(defaultValue = "1", name = "page") Integer page,
        @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize) {

        String userId = request.getHeader(HEADER_USER_ID);

        PagedGridResult result = friendRequestService.queryNewFriendList(userId,
                                                                        page,
                                                                         pageSize);
        return GraceJSONResult.ok(result);
    }

    @PostMapping("pass")
    public GraceJSONResult pass(String friendRequestId, String friendRemark) {
        friendRequestService.passNewFriend(friendRequestId, friendRemark);
        return GraceJSONResult.ok();
    }

}

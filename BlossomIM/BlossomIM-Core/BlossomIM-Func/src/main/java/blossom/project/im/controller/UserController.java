package blossom.project.im.controller;

import blossom.project.im.Users;
import blossom.project.im.base.BaseInfoProperties;
import blossom.project.im.bo.ModifyUserBO;
import blossom.project.im.grace.result.GraceJSONResult;
import blossom.project.im.grace.result.ResponseStatusEnum;
import blossom.project.im.vo.UsersVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import blossom.project.im.service.UsersService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @Auther ZhangBlossom
 */
@RestController
@RequestMapping("userinfo")
public class UserController extends BaseInfoProperties {

    @Resource
    private UsersService userService;

    @PostMapping("modify")
    public GraceJSONResult modify(@RequestBody ModifyUserBO userBO) {

        // 修改用户信息
        userService.modifyUserInfo(userBO);

        // 返回最新用户信息
        UsersVO usersVO = getUserInfo(userBO.getUserId(), true);

        return GraceJSONResult.ok(usersVO);
    }

    private UsersVO getUserInfo(String userId, boolean needToken) {

        // 查询获得用户的最新信息
        Users latestUser = userService.getById(userId);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(latestUser, usersVO);

        if (needToken) {
            String uToken = TOKEN_USER_PREFIX + SYMBOL_DOT + UUID.randomUUID();
            // 本方式只能限制用户在一台设备进行登录
            // redis.set(REDIS_USER_TOKEN + ":" + userId, uToken);   // 设置分布式会话
            // 本方式允许用户在多端多设备进行登录
            redis.set(REDIS_USER_TOKEN + ":" + uToken, userId);   // 设置分布式会话
            usersVO.setUserToken(uToken);
        }

        return usersVO;
    }

    @PostMapping("get")
    public GraceJSONResult get(@RequestParam("userId") String userId) {
        return GraceJSONResult.ok(getUserInfo(userId, false));
    }

    @PostMapping("updateFace")
    public GraceJSONResult updateFace(@RequestParam("userId") String userId,
                                      @RequestParam("face") String face) {

        ModifyUserBO userBO = new ModifyUserBO();
        userBO.setUserId(userId);
        userBO.setFace(face);

        // 修改用户信息
        userService.modifyUserInfo(userBO);

        // 返回最新用户信息
        UsersVO usersVO = getUserInfo(userBO.getUserId(), true);

        return GraceJSONResult.ok(usersVO);
    }

    @PostMapping("updateFriendCircleBg")
    public GraceJSONResult updateFriendCircleBg(
                                @RequestParam("userId") String userId,
                                @RequestParam("friendCircleBg") String friendCircleBg) {

        ModifyUserBO userBO = new ModifyUserBO();
        userBO.setUserId(userId);
        userBO.setFriendCircleBg(friendCircleBg);

        // 修改用户信息
        userService.modifyUserInfo(userBO);

        // 返回最新用户信息
        UsersVO usersVO = getUserInfo(userBO.getUserId(), true);

        return GraceJSONResult.ok(usersVO);
    }

    @PostMapping("updateChatBg")
    public GraceJSONResult updateChatBg(
            @RequestParam("userId") String userId,
            @RequestParam("chatBg") String chatBg) {
        return GraceJSONResult.ok(commonDealUpdateUserInfo(userId, chatBg));
    }

    private UsersVO commonDealUpdateUserInfo(String userId, String chatBg) {
        ModifyUserBO userBO = new ModifyUserBO();
        userBO.setUserId(userId);
        userBO.setChatBg(chatBg);

        // 修改用户信息
        userService.modifyUserInfo(userBO);

        // 返回最新用户信息
        UsersVO usersVO = getUserInfo(userBO.getUserId(), true);
        return usersVO;
    }

    @PostMapping("queryFriend")
    public GraceJSONResult queryFriend(String queryString, HttpServletRequest request) {

        if (StringUtils.isBlank(queryString)) {
            return GraceJSONResult.error();
        }

        Users friend = userService.getByWechatNumOrMobile(queryString);
        if (friend == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FRIEND_NOT_EXIST_ERROR);
        }

        // 判断，不能添加自己为好友
        String myId = request.getHeader(HEADER_USER_ID);
        if (myId.equals(friend.getId())) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.CAN_NOT_ADD_SELF_FRIEND_ERROR);
        }

        return GraceJSONResult.ok(friend);
    }

}

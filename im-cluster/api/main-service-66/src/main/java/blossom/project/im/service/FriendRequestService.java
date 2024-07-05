package blossom.project.im.service;

import blossom.project.pojo.Users;
import blossom.project.pojo.bo.ModifyUserBO;
import blossom.project.pojo.bo.NewFriendRequestBO;
import blossom.project.utils.PagedGridResult;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 好友请求 服务类
 * </p>
 *
 * @author 风间影月
 * @since 2024-03-27
 */
public interface FriendRequestService {

    /**
     * 新增添加好友的请求
     * @param friendRequestBO
     */
    public void addNewRequest(NewFriendRequestBO friendRequestBO);

    /**
     * 查询新朋友的请求记录列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryNewFriendList(String userId,
                                              Integer page,
                                              Integer pageSize);

    /**
     * 通过好友请求
     * @param friendRequestId
     * @param friendRemark
     */
    public void passNewFriend(String friendRequestId, String friendRemark);
}

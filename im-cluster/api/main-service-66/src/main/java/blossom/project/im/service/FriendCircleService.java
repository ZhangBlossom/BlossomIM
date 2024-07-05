package blossom.project.im.service;


import blossom.project.im.FriendCircleLiked;
import blossom.project.im.bo.FriendCircleBO;
import blossom.project.im.utils.PagedGridResult;

import java.util.List;

public interface FriendCircleService {

    /**
     * 发布朋友圈图文数据，保存到数据库
     * @param friendCircleBO
     */
    public void publish(FriendCircleBO friendCircleBO);

    /**
     * 分页查询朋友圈图文列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryList(String userId,
                                     Integer page,
                                     Integer pageSize);

    /**
     * 点赞朋友圈
     * @param friendCircleId
     * @param userId
     */
    public void like(String friendCircleId, String userId);

    /**
     * 取消(删除)点赞朋友圈
     * @param friendCircleId
     * @param userId
     */
    public void unlike(String friendCircleId, String userId);

    /**
     * 查询朋友圈的点赞列表
     * @param friendCircleId
     * @return
     */
    public List<FriendCircleLiked> queryLikedFriends(String friendCircleId);

    /**
     * 判断当前用户是否点赞过朋友圈
     * @param friendCircleId
     * @param userId
     * @return
     */
    public boolean doILike(String friendCircleId, String userId);

    /**
     * 删除朋友圈图文数据
     * @param friendCircleId
     * @param userId
     */
    public void delete(String friendCircleId, String userId);
}

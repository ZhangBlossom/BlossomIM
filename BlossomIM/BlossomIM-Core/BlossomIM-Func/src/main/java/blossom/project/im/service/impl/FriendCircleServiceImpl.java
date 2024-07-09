package blossom.project.im.service.impl;

import blossom.project.im.FriendCircle;
import blossom.project.im.FriendCircleLiked;
import blossom.project.im.Users;
import blossom.project.im.base.BaseInfoProperties;
import blossom.project.im.bo.FriendCircleBO;
import blossom.project.im.mapper.FriendCircleLikedMapper;
import blossom.project.im.mapper.FriendCircleMapper;
import blossom.project.im.mapper.FriendCircleMapperCustom;
import blossom.project.im.service.FriendCircleService;
import blossom.project.im.service.UsersService;
import blossom.project.im.utils.PagedGridResult;
import blossom.project.im.vo.FriendCircleVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FriendCircleServiceImpl extends BaseInfoProperties implements FriendCircleService {

    @Resource
    private FriendCircleMapper friendCircleMapper;

    @Resource
    private FriendCircleMapperCustom friendCircleMapperCustom;

    @Resource
    private FriendCircleLikedMapper circleLikedMapper;

    @Resource
    private UsersService usersService;

    @Transactional
    @Override
    public void publish(FriendCircleBO friendCircleBO) {

        FriendCircle pendingFriendCircle = new FriendCircle();

        BeanUtils.copyProperties(friendCircleBO, pendingFriendCircle);

        friendCircleMapper.insert(pendingFriendCircle);
    }

    @Override
    public PagedGridResult queryList(String userId,
                                     Integer page,
                                     Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        // 设置分页参数
        Page<FriendCircleVO> pageInfo = new Page<>(page, pageSize);
        friendCircleMapperCustom.queryFriendCircleList(pageInfo, map);

        return setterPagedGridPlus(pageInfo);
    }

    @Transactional
    @Override
    public void like(String friendCircleId, String userId) {

        // 根据朋友圈的主键ID查询归属人(发布人)
        FriendCircle friendCircle = this.selectFriendCircle(friendCircleId);

        // 根据用户主键ID查询点赞人
        Users users = usersService.getById(userId);

        FriendCircleLiked circleLiked = new FriendCircleLiked();
        circleLiked.setFriendCircleId(friendCircleId);
        circleLiked.setBelongUserId(friendCircle.getUserId());
        circleLiked.setLikedUserId(userId);
        circleLiked.setLikedUserName(users.getNickname());
        circleLiked.setCreatedTime(LocalDateTime.now());

        circleLikedMapper.insert(circleLiked);

        // 点赞过后，朋友圈的对应点赞数累加1
        redis.increment(REDIS_FRIEND_CIRCLE_LIKED_COUNTS + ":" + friendCircleId, 1);
        // 标记哪个用户点赞过该朋友圈
        redis.setnx(REDIS_DOES_USER_LIKE_FRIEND_CIRCLE + ":" + friendCircleId + ":" + userId, userId);
    }

    @Transactional
    @Override
    public void unlike(String friendCircleId, String userId) {
        // 从数据库中删除点赞关系
        QueryWrapper deleteWrapper = new QueryWrapper<FriendCircleLiked>()
                .eq("friend_circle_id", friendCircleId)
                .eq("liked_user_id", userId);
        circleLikedMapper.delete(deleteWrapper);

        // 取消点赞过后，朋友圈的对应点赞数累减1
        redis.decrement(REDIS_FRIEND_CIRCLE_LIKED_COUNTS + ":" + friendCircleId, 1);

        // 删除标记的那个用户点赞过的朋友圈
        redis.del(REDIS_DOES_USER_LIKE_FRIEND_CIRCLE + ":" + friendCircleId + ":" + userId);
    }

    @Override
    public List<FriendCircleLiked> queryLikedFriends(String friendCircleId) {
        QueryWrapper queryWrapper = new QueryWrapper<FriendCircleLiked>()
                .eq("friend_circle_id", friendCircleId);
        return circleLikedMapper.selectList(queryWrapper);
    }

    @Override
    public boolean doILike(String friendCircleId, String userId) {
        String isExist = redis.get(REDIS_DOES_USER_LIKE_FRIEND_CIRCLE + ":" + friendCircleId + ":" + userId);
        return StringUtils.isNotBlank(isExist);
    }

    @Transactional
    @Override
    public void delete(String friendCircleId, String userId) {

        QueryWrapper<FriendCircle> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("id", friendCircleId);
        deleteWrapper.eq("user_id", userId);

        friendCircleMapper.delete(deleteWrapper);



    }

    private FriendCircle selectFriendCircle(String friendCircleId) {
        return friendCircleMapper.selectById(friendCircleId);
    }
}

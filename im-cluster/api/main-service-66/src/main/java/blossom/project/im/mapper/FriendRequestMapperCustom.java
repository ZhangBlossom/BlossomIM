package blossom.project.im.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import blossom.project.pojo.FriendRequest;
import blossom.project.pojo.vo.NewFriendsVO;

import java.util.Map;

/**
 * <p>
 * 好友请求记录表 Mapper 接口
 * </p>
 *
 * @author 风间影月
 * @since 2024-03-27
 */
public interface FriendRequestMapperCustom {

    public Page<NewFriendsVO> queryNewFriendList(@Param("page") Page<NewFriendsVO> page,
                                                 @Param("paramMap") Map<String, Object> map);

}

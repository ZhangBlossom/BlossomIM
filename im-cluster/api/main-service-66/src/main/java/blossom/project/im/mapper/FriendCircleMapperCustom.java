package blossom.project.im.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import blossom.project.pojo.FriendCircle;
import blossom.project.pojo.vo.FriendCircleVO;
import blossom.project.pojo.vo.NewFriendsVO;

import java.util.Map;

/**
 * <p>
 * 朋友圈表 Mapper 接口
 * </p>
 *
 * @author 风间影月
 * @since 2024-03-27
 */
public interface FriendCircleMapperCustom {

    public Page<FriendCircleVO> queryFriendCircleList(
            @Param("page") Page<FriendCircleVO> page,
            @Param("paramMap") Map<String, Object> map);

}

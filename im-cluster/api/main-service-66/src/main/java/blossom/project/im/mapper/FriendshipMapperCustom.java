package blossom.project.im.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import blossom.project.pojo.Friendship;
import blossom.project.pojo.vo.ContactsVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 朋友关系表 Mapper 接口
 * </p>
 *
 * @author 风间影月
 * @since 2024-03-27
 */
public interface FriendshipMapperCustom {

    public List<ContactsVO> queryMyFriends(@Param("paramMap") Map<String, Object> map);

}

package blossom.project.im.mapper;

import org.apache.ibatis.annotations.Param;
import blossom.project.pojo.vo.CommentVO;

import java.util.List;
import java.util.Map;

public interface CommentMapperCustom {

    public List<CommentVO> queryFriendCircleComments(@Param("paramMap") Map<String, Object> map);

}

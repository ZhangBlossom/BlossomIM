package blossom.project.im.mapper;

import blossom.project.im.vo.CommentVO;
import org.apache.ibatis.annotations.Param;


import java.util.List;
import java.util.Map;

public interface CommentMapperCustom {

    public List<CommentVO> queryFriendCircleComments(@Param("paramMap") Map<String, Object> map);

}

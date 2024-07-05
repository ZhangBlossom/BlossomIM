package blossom.project.im.service;

import blossom.project.pojo.bo.CommentBO;
import blossom.project.pojo.vo.CommentVO;

import java.util.List;


public interface CommentService {

    /**
     * 创建发表评论
     * @param commentBO
     */
    public CommentVO createComment(CommentBO commentBO);

    /**
     * 查询朋友圈的列表
     * @param friendCircleId
     * @return
     */
    public List<CommentVO> queryAll(String friendCircleId);

    /**
     * 删除朋友圈的评论
     * @param commentUserId
     * @param commentId
     * @param friendCircleId
     */
    public void deleteComment(String commentUserId,
                              String commentId,
                              String friendCircleId);

}

package blossom.project.im.service;

import blossom.project.pojo.netty.ChatMsg;
import blossom.project.utils.PagedGridResult;
import org.springframework.web.bind.annotation.PathVariable;

public interface ChatMessageService {

    /**
     * 保存聊天信息
     * @param chatMsg
     */
    public void saveMsg(ChatMsg chatMsg);

    /**
     * 查询聊天信息列表
     * @param senderId
     * @param receiverId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryChatMsgList(String senderId,
                                            String receiverId,
                                            Integer page,
                                            Integer pageSize);

    /**
     * 标记语音聊天信息的签收已读
     * @param msgId
     */
    public void updateMsgSignRead(String msgId);

}

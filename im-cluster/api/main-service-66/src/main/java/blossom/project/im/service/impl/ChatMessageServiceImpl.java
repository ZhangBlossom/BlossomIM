package blossom.project.im.service.impl;

import blossom.project.im.service.ChatMessageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import blossom.project.base.BaseInfoProperties;
import blossom.project.im.mapper.ChatMessageMapper;
import blossom.project.pojo.ChatMessage;
import blossom.project.pojo.netty.ChatMsg;
import blossom.project.utils.PagedGridResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMessageServiceImpl extends BaseInfoProperties implements ChatMessageService {

    @Resource
    private ChatMessageMapper chatMessageMapper;

    @Transactional
    @Override
    public void saveMsg(ChatMsg chatMsg) {

        ChatMessage message = new ChatMessage();
        BeanUtils.copyProperties(chatMsg, message);

        // 手动设置聊天信息的主键id
        message.setId(chatMsg.getMsgId());

        chatMessageMapper.insert(message);

        String receiverId = chatMsg.getReceiverId();
        String senderId = chatMsg.getSenderId();

        // 通过redis累加信息接受者的对应记录
        redis.incrementHash(CHAT_MSG_LIST + ":" + receiverId, senderId, 1);
    }

    @Override
    public PagedGridResult queryChatMsgList(String senderId,
                                            String receiverId,
                                            Integer page,
                                            Integer pageSize) {

        Page<ChatMessage> pageInfo = new Page<>(page, pageSize);

        QueryWrapper queryWrapper = new QueryWrapper<ChatMessage>()
                .or(qw -> qw.eq("sender_id", senderId)
                            .eq("receiver_id", receiverId))
                .or(qw -> qw.eq("sender_id", receiverId)
                            .eq("receiver_id", senderId))
                .orderByDesc("chat_time");

        chatMessageMapper.selectPage(pageInfo, queryWrapper);

        // 获得列表后，倒着排序，因为聊天记录是展现最新的数据在聊天框的最下方，旧的数据在上方
        // 逆向逆序的处理
        List<ChatMessage> list = pageInfo.getRecords();
        List<ChatMessage> msgList  = list.stream().sorted(
                Comparator.comparing(ChatMessage::getChatTime)
        ).collect(Collectors.toList());

        pageInfo.setRecords(msgList);

        return setterPagedGridPlus(pageInfo);
    }

    @Transactional
    @Override
    public void updateMsgSignRead(String msgId) {

        ChatMessage message = new ChatMessage();
        message.setId(msgId);
        message.setIsRead(true);

        chatMessageMapper.updateById(message);
    }
}

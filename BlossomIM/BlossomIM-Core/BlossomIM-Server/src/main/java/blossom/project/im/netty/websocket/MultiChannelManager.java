package blossom.project.im.netty.websocket;

import blossom.project.im.netty.DataContent;
import blossom.project.im.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会话管理
 * 用户id和channel的关联处理
 * @Auther ZhangBlossom
 */
public class MultiChannelManager {

    // 用于多端同时接受消息，允许同一个账号在多个设备同时在线，比如iPad、iPhone、Mac等设备同时收到消息
    // key: userId, value: 多个用户的channel
    private static Map<String, List<Channel>> multiChannel = new HashMap<>();

    // 用于记录用户id和客户端channel长id的关联关系
    private static Map<String, String> userChannelIdRelation = new HashMap<>();

    public static void putUserChannelIdRelation(String channelId, String userId) {
        userChannelIdRelation.put(channelId, userId);
    }
    public static String getUserIdByChannelId(String channelId) {
        return userChannelIdRelation.get(channelId);
    }

    public static void putMultiChannels(String userId, Channel channel) {

        List<Channel> channels = getMultiChannels(userId);
        if (channels == null || channels.size() == 0) {
            channels = new ArrayList<>();
        }
        channels.add(channel);

        multiChannel.put(userId, channels);
    }
    public static List<Channel> getMultiChannels(String userId) {
        return multiChannel.get(userId);
    }

    public static void removeUselessChannels(String userId, String channelId) {

        List<Channel> channels = getMultiChannels(userId);
        if (channels == null || channels.size() == 0) {
            return;
        }

        for (int i = 0 ; i < channels.size() ; i ++) {
            Channel tempChannel = channels.get(i);
            if (tempChannel.id().asLongText().equals(channelId)) {
                channels.remove(i);
            }
        }

        multiChannel.put(userId, channels);
    }

    public static List<Channel> getMyOtherChannels(String userId, String channelId) {
        List<Channel> channels = getMultiChannels(userId);
        if (channels == null || channels.size() == 0) {
            return null;
        }

        List<Channel> myOtherChannels = new ArrayList<>();
        for (int i = 0 ; i < channels.size() ; i ++) {
            Channel tempChannel = channels.get(i);
            if (!tempChannel.id().asLongText().equals(channelId)) {
                myOtherChannels.add(tempChannel);
            }
        }
        return myOtherChannels;
    }

    public static void outputMulti() {
        for (Map.Entry<String, List<Channel>> entry : multiChannel.entrySet()) {
            System.out.println("UserId: " + entry.getKey());
            List<Channel> temp = entry.getValue();
            for (Channel c : temp) {
                System.out.println("\t\t ChannelId: " + c.id().asLongText());
            }
        }
    }

    public static void sendToTarget(List<Channel> receiverChannels, DataContent dataContent) {

        ChannelGroup clients = ChatHandler.clients;

        if (receiverChannels == null) {
            return;
        }

        for (Channel c : receiverChannels) {
            Channel findChannel = clients.find(c.id());
            if (findChannel != null) {
                findChannel.writeAndFlush(
                        new TextWebSocketFrame(
                                JsonUtils.objectToJson(dataContent)));
            }

        }
    }

    public static void sendToMyOthers(List<Channel> myOtherChannels, DataContent dataContent) {

        ChannelGroup clients = ChatHandler.clients;

        if (myOtherChannels == null) {
            return;
        }

        for (Channel c : myOtherChannels) {
            Channel findChannel = clients.find(c.id());
            if (findChannel != null) {
                findChannel.writeAndFlush(
                        new TextWebSocketFrame(
                                JsonUtils.objectToJson(dataContent)));
            }
        }
    }

}

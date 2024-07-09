package blossom.project.im.controller;

import blossom.project.im.base.BaseInfoProperties;
import blossom.project.im.grace.result.GraceJSONResult;
import blossom.project.im.netty.NettyServerNode;
import blossom.project.im.utils.JsonUtils;
import blossom.project.im.utils.PagedGridResult;
import jakarta.annotation.Resource;
import org.apache.curator.framework.CuratorFramework;

import blossom.project.im.service.ChatMessageService;

import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Auther ZhangBlossom
 */
@RestController
@RequestMapping("chat")
public class ChatController extends BaseInfoProperties {

    @Resource
    private ChatMessageService chatMessageService;

    @PostMapping("getMyUnReadCounts")
    public GraceJSONResult getMyUnReadCounts(String myId) {
        Map map = redis.hgetall(CHAT_MSG_LIST + ":" + myId);
        return GraceJSONResult.ok(map);
    }

    @PostMapping("clearMyUnReadCounts")
    public GraceJSONResult clearMyUnReadCounts(String myId, String oppositeId) {
        redis.setHashValue(CHAT_MSG_LIST + ":" + myId, oppositeId, "0");
        return GraceJSONResult.ok();
    }

    @PostMapping("list/{senderId}/{receiverId}")
    public GraceJSONResult list(@PathVariable("senderId") String senderId,
                                @PathVariable("receiverId") String receiverId,
                                Integer page,
                                Integer pageSize) {

        if (page == null) page = 1;
        if (pageSize == null) page = 20;

        PagedGridResult gridResult = chatMessageService.queryChatMsgList(
                                                                senderId,
                                                                receiverId,
                                                                page,
                                                                pageSize);
        return GraceJSONResult.ok(gridResult);
    }

    @PostMapping("signRead/{msgId}")
    public GraceJSONResult signRead(@PathVariable("msgId") String msgId) {
        chatMessageService.updateMsgSignRead(msgId);
        return GraceJSONResult.ok();
    }

    @Resource(name = "curatorClient")
    private CuratorFramework zkClient;

    @PostMapping("getNettyOnlineInfo")
    public GraceJSONResult getNettyOnlineInfo() throws Exception {

        // 从zookeeper中获得当前已经注册的netty 服务列表
        String path = "/server-list";
        List<String> list = zkClient.getChildren().forPath(path);

        List<NettyServerNode> serverNodeList = new ArrayList<>();
        for (String node:list) {
            // System.out.println(node);
            String nodeValue = new String(zkClient.getData().forPath(path + "/" + node));
            // System.out.println(nodeValue);
            NettyServerNode serverNode = JsonUtils.jsonToPojo(nodeValue, NettyServerNode.class);
            serverNodeList.add(serverNode);
        }

        // 计算当前哪个zk的node是最少连接，获得[ip:port]并且返回给前端
        Optional<NettyServerNode> minNodeOptional = serverNodeList
                .stream()
                .min(Comparator.comparing(nettyServerNode -> nettyServerNode.getOnlineCounts()));
        NettyServerNode minNode = minNodeOptional.get();

        return GraceJSONResult.ok(minNode);
    }

}

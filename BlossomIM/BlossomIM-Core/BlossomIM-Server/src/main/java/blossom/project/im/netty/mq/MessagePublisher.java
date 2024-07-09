package blossom.project.im.netty.mq;


import blossom.project.im.netty.ChatMsg;
import blossom.project.im.utils.JsonUtils;

public class MessagePublisher {

    // 定义交换机的名字
    public static final String TEST_EXCHANGE = "test_exchange";

    // 定义队列的名字
    public static final String TEST_QUEUE = "test_queue";

    // 发送信息到消息队列接受并且保存到数据库的路由地址
    public static final String ROUTING_KEY_WECHAT_MSG_SEND = "BlossomIM.wechat.wechat.msg.send";


    public static void sendMsgToSave(ChatMsg msg) throws Exception {
        RabbitMQConnectUtils connectUtils = new RabbitMQConnectUtils();
        connectUtils.sendMsg(JsonUtils.objectToJson(msg),
                            TEST_EXCHANGE,
                            ROUTING_KEY_WECHAT_MSG_SEND);
    }

    public static void sendMsgToOtherNettyServer(String msg) throws Exception {
        RabbitMQConnectUtils connectUtils = new RabbitMQConnectUtils();
        String fanout_exchange = "fanout_exchange";
        connectUtils.sendMsg(msg, fanout_exchange, "");
    }
}

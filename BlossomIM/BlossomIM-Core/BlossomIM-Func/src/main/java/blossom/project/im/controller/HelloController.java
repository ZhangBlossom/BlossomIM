package blossom.project.im.controller;


import blossom.project.im.netty.ChatMsg;
import blossom.project.im.rabbitmq.RabbitMQTestConfig;
import blossom.project.im.utils.JsonUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther ZhangBlossom
 */
@RestController
@RequestMapping("m")
public class HelloController {

    // 127.0.0.1:88/a/hello

    @GetMapping("hello")
    public Object hello() {
        return "Hello world~";
    }

    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("mq")
    public Object mq() {

        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setMsg("hello world~~~");
        String msg = JsonUtils.objectToJson(chatMsg);

        rabbitTemplate.convertAndSend(
                RabbitMQTestConfig.TEST_EXCHANGE,
                RabbitMQTestConfig.ROUTING_KEY_TEST_SEND,
                msg);

        return "ok";
    }

}

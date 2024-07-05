package blossom.project.im.zk;

import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import blossom.project.base.BaseInfoProperties;
import blossom.project.pojo.netty.NettyServerNode;
import blossom.project.utils.JsonUtils;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@ConfigurationProperties(prefix = "zookeeper.curator")
@Data
public class CuratorConfig extends BaseInfoProperties {

    private String host;                    // 单机/集群的ip:port地址
    private Integer connectionTimeoutMs;    // 连接超时时间
    private Integer sessionTimeoutMs;         // 会话超时时间
    private Integer sleepMsBetweenRetry;    // 每次重试的间隔时间
    private Integer maxRetries;             // 最大重试次数
    private String namespace;               // 命名空间（root根节点名称）

    public static final String path = "/server-list";

    @Bean("curatorClient")
    public CuratorFramework curatorClient() {
        // 三秒后重连一次，只连一次
        //RetryPolicy retryOneTime = new RetryOneTime(3000);
        // 每3秒重连一次，重连3次
        //RetryPolicy retryNTimes = new RetryNTimes(3, 3000);
        // 每3秒重连一次，总等待时间超过10秒则停止重连
        //RetryPolicy retryPolicy = new RetryUntilElapsed(10 * 1000, 3000);
        // 随着重试次数的增加，重试的间隔时间也会增加（推荐）
        RetryPolicy backoffRetry = new ExponentialBackoffRetry(sleepMsBetweenRetry, maxRetries);

        // 声明初始化客户端
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(host)
                .connectionTimeoutMs(connectionTimeoutMs)
                .sessionTimeoutMs(sessionTimeoutMs)
                .retryPolicy(backoffRetry)
                .namespace(namespace)
                .build();
        client.start();     // 启动curator客户端

        // try {
        //    client.create().forPath("/abc", "123".getBytes());
        // } catch (Exception e) {
        //    e.printStackTrace();
        // }

        // 注册事件
        add(path, client);

        return client;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private RabbitAdmin rabbitAdmin;

    /**
     * 注册节点的事件监听
     * @param path
     * @param client
     */
    public void add(String path, CuratorFramework client) {

        CuratorCache curatorCache = CuratorCache.build(client, path);
        curatorCache.listenable().addListener((type, oldData, data) -> {
            // type: 当前监听到的事件类型
            // oldData: 节点更新前的数据、状态
            // data: 节点更新后的数据、状态

            System.out.println(type.name());

            // System.out.println("new path:" + data.getPath() + ",
            // new value:" + data.getData());

            //NODE_CREATED
            //NODE_CHANGED
            //NODE_DELETED

            switch (type.name()) {
                case "NODE_CREATED":
                    log.info("(子)节点创建");
                    break;
                case "NODE_CHANGED":
                    log.info("(子)节点数据变更");
                    break;
                case "NODE_DELETED":
                    log.info("(子)节点删除");

                    NettyServerNode oldNode = JsonUtils.jsonToPojo(new String(oldData.getData()),
                                                                   NettyServerNode.class);

                    System.out.println("old path:" + oldData.getPath() + ", old value:" + oldNode);

                    String oldPort = oldNode.getPort() + "";
                    String portKey = "netty_port";
                    redis.hdel(portKey, oldPort);

                    String queueName = "netty_queue_" + oldPort;
                    rabbitAdmin.deleteQueue(queueName);

                    break;
                default:
                    log.info("default");
                    break;
            }

        });

        curatorCache.start();
    }

}

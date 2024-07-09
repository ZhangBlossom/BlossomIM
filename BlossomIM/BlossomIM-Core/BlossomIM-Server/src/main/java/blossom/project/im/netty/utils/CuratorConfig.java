package blossom.project.im.netty.utils;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CuratorConfig {

    private static String host = "127.0.0.1:3191";                 // 单机/集群的ip:port地址
    private static Integer connectionTimeoutMs = 30 * 1000;        // 连接超时时间
    private static Integer sessionTimeoutMs = 3 * 1000;            // 会话超时时间
    private static Integer sleepMsBetweenRetry = 2 * 1000;         // 每次重试的间隔时间
    private static Integer maxRetries = 3;                         // 最大重试次数
    private static String namespace = "itzixi-im";                 // 命名空间（root根节点名称）

    private static CuratorFramework client;

    static {
        RetryPolicy backoffRetry = new ExponentialBackoffRetry(sleepMsBetweenRetry, maxRetries);

        // 声明初始化客户端
        client = CuratorFrameworkFactory.builder()
                .connectString(host)
                .connectionTimeoutMs(connectionTimeoutMs)
                .sessionTimeoutMs(sessionTimeoutMs)
                .retryPolicy(backoffRetry)
                .namespace(namespace)
                .build();
        client.start();     // 启动curator客户端
    }

    public static CuratorFramework getClient() {
        return client;
    }

}

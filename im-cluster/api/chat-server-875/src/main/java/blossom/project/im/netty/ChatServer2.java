package blossom.project.im.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import blossom.project.im.netty.mq.RabbitMQConnectUtils;
import blossom.project.im.netty.utils.JedisPoolUtils;
import blossom.project.im.netty.utils.ZookeeperRegister;
import blossom.project.im.netty.websocket.WSServerInitializer;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ChatServer: Netty 服务的启动类(服务器)
 * @Auther 风间影月
 */
public class ChatServer2 {

    public static final Integer nettyDefaultPort = 875;
    public static final String initOnlineCounts = "0";

    /*
     * FIXME: 优化方案，改成zookeeper方案，
     *        如此可以不需要在中断连接后，监听并且清理在线人数和端口，
     *        因为netty与zk建立的临时节点，中断连接后，会自动删除该临时节点
     */
    public static Integer selectPort(Integer port) {
        String portKey = "netty_port";
        Jedis jedis = JedisPoolUtils.getJedis();

        jedis.set("jedis-test", "hello world");

        Map<String, String> portMap = jedis.hgetAll(portKey);
        System.out.println(portMap);
        // 由于map中的key都应该是整数类型的port，所以先转换成整数后，再比对，否则string类型的比对会有问题
        List<Integer> portList = portMap.entrySet().stream()
                .map(entry -> Integer.valueOf(entry.getKey()))
                .collect(Collectors.toList());
        // step1: 编码到此处先运行测试看一下结果
        System.out.println(portList);

        Integer nettyPort = null;
        if (portList == null || portList.isEmpty()) {
            // step2: 编码到此处先运行测试看一下结果
            jedis.hset(portKey, port+"", initOnlineCounts);
            nettyPort = port;
        } else {
            // 循环portList，获得最大值，并且累加10
            Optional<Integer> maxInteger = portList.stream().max(Integer::compareTo);
            Integer maxPort = maxInteger.get().intValue();
            Integer currentPort = maxPort + 10;
            jedis.hset(portKey, currentPort+"", initOnlineCounts);
            nettyPort = currentPort;
        }
        // step3: 编码到此处先运行测试看一下最终结果
        return nettyPort;

        /**
         * TODO: ChatServer停止的时候，需要删除在redis中对应的端口。
         *       一旦断开，则会触发zk的节点删除事件，在那边删除即可。写到springboot服务中即可
         */


        // TODO: 客户端负载均衡，最小连接数，查询redis中最少在线人数的，并且让前端建立ws连接
        // TODO: 用户创建连接，会话管理那边，则对应netty服务的在线人数累+1
        // TODO: 用户断开连接，会话管理那边，则对应netty服务的在线人数累-1
    }

    public static void removePort(Integer port) {
        String portKey = "netty_port";
        Jedis jedis = JedisPoolUtils.getJedis();
        jedis.hdel(portKey, port+"");
    }

    public static void main(String[] args) throws Exception {

        // 定义主从线程组
        // 定义主线程池，用于接受客户端的连接，但是不做任何处理，比如老板会谈业务，拉到业务就会交给下面的员工去做了
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 定义从线程池，处理主线程池交过来的任务，公司业务员开展业务，完成老板交代的任务
        EventLoopGroup workerGroup = new NioEventLoopGroup();


        // Netty服务启动的时候，从redis中查找有没有端口，如果没有则用875，如果有则把端口累加1(或10)再启动
        Integer nettyPort = selectPort(nettyDefaultPort);



        // 注册当前netty服务到zookeeper中
        ZookeeperRegister.registerNettyServer("server-list",
                                                ZookeeperRegister.getLocalIp(),
                                                nettyPort);



        // 启动消费者进行监听，队列可以根据动态生成的端口号进行拼接
        String queueName = "netty_queue_" + nettyPort;
        RabbitMQConnectUtils mqConnectUtils = new RabbitMQConnectUtils();
        mqConnectUtils.listen("fanout_exchange", queueName);




        try {
            // 构建Netty服务器
            ServerBootstrap server = new ServerBootstrap();     // 服务的启动类
            server.group(bossGroup, workerGroup)                // 把主从线程池组放入到启动类中
                    .channel(NioServerSocketChannel.class)      // 设置Nio的双向通道
                    .childHandler(new WSServerInitializer());   // 设置处理器，用于处理workerGroup

            // 启动server，并且绑定端口号875，同时启动方式为"同步"
            ChannelFuture channelFuture = server.bind(nettyPort).sync();
            // 请求：http://127.0.0.1:875

            // 监听关闭的channel
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 优雅的关闭线程池组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            // 移除现有的redis与netty的端口关系
            removePort(nettyPort);
        }
    }

}

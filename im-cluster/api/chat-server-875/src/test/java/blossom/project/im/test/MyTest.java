package blossom.project.im.test;

import org.apache.commons.lang3.StringUtils;
import blossom.project.im.netty.utils.JedisPoolUtils;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Auther ZhangBlossom
 */
public class MyTest {

    /**
     * 测试Jedis连接池
     */
    // @Test
    // public void testJedisPool() {
    //     String key = "testJedis";
    //     Jedis jedis = JedisPoolUtils.getJedis();
    //     jedis.set(key, "hello world~~");
    //     String cacheValue = jedis.get(key);
    //     System.out.println(cacheValue);
    // }

    /**
     * 测试Jedis自动创建Netty端口与切换
     */
    @Test
    public void testJedisGetNettyPorts() {
        Integer nettyPort = selectPort(nettyDefaultPort);
        System.out.println(nettyPort);
    }

    public static final Integer nettyDefaultPort = 875;
    public static final String initOnlineCounts = "0";

    public static Integer selectPort(Integer port) {
        String portKey = "netty_port";
        Jedis jedis = JedisPoolUtils.getJedis();

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
    }

}

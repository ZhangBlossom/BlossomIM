package blossom.project.im.controller;


import blossom.project.im.base.BaseInfoProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther ZhangBlossom
 */
@RestController
@RequestMapping("g")
public class HelloController extends BaseInfoProperties {

    // @Resource
    // private RedisOperator redis;

    // 127.0.0.1:1000/g/hello

    @GetMapping("hello")
    public Object hello() {
        return "Hello gateway~";
    }

    @GetMapping("setRedis")
    public Object setRedis(String k, String v) {
        redis.set(k, v);
        return "setRedis OK~";
    }

    @GetMapping("getRedis")
    public Object setRedis(String k) {
        return redis.get(k);
    }

}

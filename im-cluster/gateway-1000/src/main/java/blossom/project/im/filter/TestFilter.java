package blossom.project.im.filter;

import lombok.extern.slf4j.Slf4j;
import blossom.project.base.BaseInfoProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Auther 风间影月
 */
@Component
@Slf4j
public class TestFilter extends BaseInfoProperties implements GlobalFilter, Ordered {

    /**
     * 需求：
     * 判断某个请求的ip在20秒内的请求次数是否超过3次
     * 如果超过3次，则限制访问30秒
     * 等待30秒静默后，才能够继续恢复访问
     */

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        log.info("TEST 当前的执行顺序order为10");

        // 默认放行请求到后续的路由(服务)
        return chain.filter(exchange);
    }

    // 过滤器的顺序，数字越小则优先级越大
    @Override
    public int getOrder() {
        return 10;
    }
}

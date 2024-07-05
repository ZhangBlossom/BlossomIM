package blossom.project.im.filter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import blossom.project.base.BaseInfoProperties;
import blossom.project.grace.result.ResponseStatusEnum;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Auther 风间影月
 */
@Component
@Slf4j
public class SecurityFilterToken extends BaseInfoProperties implements GlobalFilter, Ordered {

    @Resource
    private ExcludeUrlProperties excludeUrlProperties;

    // 路径匹配规则器
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1. 获得当前用户请求的路径url
        String url = exchange.getRequest().getURI().getPath();
        log.info("SecurityFilterToken url = {}", url);

        // 2. 获得所有的需要排除校验的url list
        List<String> excludeList = excludeUrlProperties.getUrls();

        // 3.1 校验并且排除excludeList
        if (excludeList != null && !excludeList.isEmpty()) {
            for (String excludeUrl : excludeList) {
                if (antPathMatcher.matchStart(excludeUrl, url)) {
                    // 如果匹配到，则直接放行，表示当前的url是不需要被拦截校验的
                    return chain.filter(exchange);
                }
            }
        }

        // 3.1 排除静态资源服务static
        String fileStart = excludeUrlProperties.getFileStart();
        if (StringUtils.isNotBlank(fileStart)) {
            boolean matchFileStart = antPathMatcher.matchStart(fileStart, url);
            if (matchFileStart) return chain.filter(exchange);
        }

        // 4. 代码到达此处，表示请求被拦截，需要进行校验
        log.info("当前请求的路径[{}]被拦截...", url);

        // 5. 从header中获得用户的id以及token
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String userId = headers.getFirst(HEADER_USER_ID);
        String userToken = headers.getFirst(HEADER_USER_TOKEN);
        log.info("userId = {}", userId);
        log.info("userToken = {}", userToken);

        // 6. 判断header中是否有token，对用户请求进行判断拦截
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            // 限制只能单设备登录
            // String redisToken = redis.get(REDIS_USER_TOKEN + ":" + userId);
            // if (redisToken.equals(userToken)) {
            //     // 匹配则放行
            //     return chain.filter(exchange);
            // }

            // 允许多设备登录
            String userIdRedis = redis.get(REDIS_USER_TOKEN + ":" + userToken);
            if (userIdRedis.equals(userId)) {
                // 匹配则放行
                return chain.filter(exchange);
            }
        }

        // 默认不放行
        return RenderErrorUtils.display(exchange, ResponseStatusEnum.UN_LOGIN);
    }

    // 过滤器的顺序，数字越小则优先级越大
    @Override
    public int getOrder() {
        return 0;
    }
}

package blossom.project.im;

import blossom.project.im.controller.interceptor.SMSInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Auther ZhangBlossom
 */
@Configuration
public class InterceptorConifg implements WebMvcConfigurer {

    /**
     * 在Springboot容器中放入拦截器
     * @return
     */
    @Bean
    public SMSInterceptor smsInterceptor() {
        return new SMSInterceptor();
    }

    /**
     * 注册拦截器，并且拦截指定的路由，否则不生效
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(smsInterceptor())
                .addPathPatterns("/passport/getSMSCode");
    }
}

package com.softkey.F2k.config;

import com.softkey.F2k.interceptor.F2kInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 描述：加密锁拦截器配置
 * @Author shf
 * @Description TODO
 * @Date 2019/4/15 10:06
 * @Version V1.0
 **/
@Configuration
public class F2kWebAdapterConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(new F2kInterceptor());
        registration.addPathPatterns("/**");//需要拦截的请求
        registration.excludePathPatterns("/error");//不需要拦截的请求
    }
}

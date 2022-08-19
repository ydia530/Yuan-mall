package com.yuan.mall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Yuan Diao
 * @date 2022/8/16
 */
@Configuration
public class FeignConfig implements RequestInterceptor {
    public static final String LOGIN_TOKEN_KEY = "Mall-Token";
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //获取到进入时的request对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null != attributes) {
            HttpServletRequest request = attributes.getRequest();
            if(request != null){
                // 同步请求头数据
                String token = request.getHeader(LOGIN_TOKEN_KEY);
                System.out.println(token);
                // 给新请求同步Cookie
                requestTemplate.header(LOGIN_TOKEN_KEY, token);
            }
        }
    }

}

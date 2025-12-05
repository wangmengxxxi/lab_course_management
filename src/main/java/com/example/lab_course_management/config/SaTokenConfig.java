package com.example.lab_course_management.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Sa-Token 配置
 *
 * @author dddwmx
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 指定一系列接口，不需要登录校验（白名单）
            SaRouter
                    .match("/**")
                    .notMatch("/user/login", "/user/register")
                    .notMatch("/doc.html", "/webjars/**", "/swagger-resources/**", "/v3/api-docs/**",
                             "/v3/api-docs", "/swagger-ui/**", "/swagger-ui.html",
                             "/knife4j/**", "/doc.html#/home")
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}
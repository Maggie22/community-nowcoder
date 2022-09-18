package com.nowcoder.community.config;

import com.nowcoder.community.controller.interceptor.LoginInterceptor;
import com.nowcoder.community.controller.interceptor.LoginRequiredInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    LoginInterceptor loginInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png", "/**/*.jpeg", "/verify-image");

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png", "/**/*.jpeg");

    }

}

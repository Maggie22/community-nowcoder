package com.nowcoder.community.config;

import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig{

    @Bean
    WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring().antMatchers("/resources/**");
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow")
                .hasAnyAuthority(
                        CommunityConstant.AUTHORITY_USER,
                        CommunityConstant.AUTHORITY_ADMIN,
                        CommunityConstant.AUTHORITY_MODERATOR
                )
                .antMatchers("/discuss/setTop",
                            "/discuss/setRecommend")
                .hasAnyAuthority(
                        CommunityConstant.AUTHORITY_ADMIN,
                        CommunityConstant.AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/setInvalidate")
                .hasAnyAuthority(
                        CommunityConstant.AUTHORITY_MODERATOR
                )
                .anyRequest().permitAll();

        // 权限不够时处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    // 没有登录时
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-request-with");
                        if("XMLHttpRequest".equals(xRequestedWith)){
                            // POST请求时，比如要发帖发评论的时候
                            response.setContentType("application/json;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtils.getJSONString(403, "请先登录"));
                        }else{
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    // 权限不足导致出错时
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if("XMLHttpRequest".equals(xRequestedWith)){
                            // 异步请求时，比如要发帖发评论的时候
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtils.getJSONString(403, "权限不足"));
                        }else{
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

        // SpringSecurity会自动拦截logout请求，这样就没办法执行我们自己的逻辑了，所以需要先覆盖SpringSecurity的
        http.logout().logoutUrl("/securityLogout"); // 它原来的url是logout，把它给修改成一个没意义的

        // 再修改SpringSecurity中的登出状态(在logout中移除

        return http.build();
    }

}

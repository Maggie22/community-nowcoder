package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CookieUtils;
import com.nowcoder.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtils.getCookie(request, "ticket");
        if(ticket != null){
            // 浏览器有登录记录，然后找user，为了以后过程复用
            LoginTicket loginTicket = userService.findLoginTicket(ticket);

            // 登录状态有效才可继续
            if(loginTicket != null && loginTicket.getStatus()==CommunityConstant.LOGIN && loginTicket.getExpired().after(new Date())){
                User user = userService.findUserById(loginTicket.getUserId());
                if(user != null){
                    hostHolder.setUsers(user);
                    // 用户存入SecurityContext，便于认证
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            user, user.getPassword(), userService.getAuthorities(user.getId())
                    );
                    SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 获取User
        User user = hostHolder.getUser();
        if(user!=null && modelAndView!=null){
            int unreadCount = messageService.getUnreadNoticeCount(user.getId(), null);
            unreadCount += messageService.getCountUnreadMessage(null, user.getId());
            user.setUnreadCount(unreadCount);
            modelAndView.addObject("headerUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
        SecurityContextHolder.clearContext();
    }
}

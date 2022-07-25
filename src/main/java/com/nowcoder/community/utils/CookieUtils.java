package com.nowcoder.community.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtils {

    public static String getCookie(HttpServletRequest request, String name){
        if(request != null && !StringUtils.isBlank(name)){
            Cookie[] cookies = request.getCookies();
            if(cookies!=null){
                for(Cookie cookie: cookies){
                    if(name.equals(cookie.getName()))
                        return cookie.getValue();
                }
            }
        }
        return null;
    }
}

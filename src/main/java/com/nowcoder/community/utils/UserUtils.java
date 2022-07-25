package com.nowcoder.community.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;
import java.util.regex.Pattern;

public class UserUtils {

    public static boolean isLegalEmail(String s){
        // 需要改进，参考维基百科https://zh.m.wikipedia.org/zh-hans/%E9%9B%BB%E5%AD%90%E9%83%B5%E4%BB%B6%E5%9C%B0%E5%9D%80
        String pattern = "^[a-zA-Z0-9_-]+@[\\.a-zA-Z0-9_-]+";
        boolean isMatch = Pattern.matches(pattern, s);
        return isMatch;
    }

    public static String isLegalPassword(String s){
        // 测试环境，要求不为空即可
        if(StringUtils.isBlank(s))
            return "密码不能为空";
//        if(s.length()<8)
//            return "密码长度不能小于8";
        return "";
    }

}

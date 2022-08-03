package com.nowcoder.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;

import java.util.HashMap;
import java.util.Map;

public class AspectUtils {

    public static Map<String, Object> getParams(JoinPoint joinPoint){
        Map<String, Object> map = new HashMap<>();
        Object[] vals = joinPoint.getArgs();
        String[] names = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        for(int i=0;i< vals.length;i++){
            map.put(names[i], vals[i]);
        }
        return map;
    }

}

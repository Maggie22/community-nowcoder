package com.nowcoder.community.aspect;

import com.nowcoder.community.utils.RedisUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 当修改数据时，删除缓存，并不是什么时候都需要修改
 */
public class DelCacheAspect {

    @Autowired
    RedisTemplate redisTemplate;

    @Pointcut("execution(* com.nowcoder.community.service.UserService.update*(..))")
    public void pointcut(){}

    @After("pointcut()")
    public void delCache(JoinPoint joinPoint){
        Map<String, Object> params = AspectUtils.getParams(joinPoint);
        int userId = (int) params.get("userId");
        String userKey = RedisUtils.getUserKey(userId);
        redisTemplate.delete(userKey);
    }
}

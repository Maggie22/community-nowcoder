package com.nowcoder.community.service;

import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    RedisTemplate redisTemplate;

    /**
     *
     * @return 返回当前用户对该实体的点赞状态
     */
    public int setLikeStatus(int userId, int entityType, int entityId, int targetUserId){
        String key = RedisUtils.getLikeKey(entityType, entityId);
        String userKey = RedisUtils.getUserLikeKey(targetUserId);
        Boolean isMember = redisTemplate.opsForSet().isMember(key, userId);
        if(isMember){
            // 该用户已经点过赞
            redisTemplate.opsForSet().remove(key, userId);
            redisTemplate.opsForValue().decrement(userKey);
            return CommunityConstant.LIKE_COMMON;
        }else {
            redisTemplate.opsForSet().add(key, userId);
            redisTemplate.opsForValue().increment(userKey);
            return CommunityConstant.LIKE_LIKED;
        }
    }

    /**
     *
     * @return 返回该实体的点赞数量
     */
    public long getTotalLike(int entityType, int entityId){
        String key = RedisUtils.getLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(key);
    }

    /**
     *
     * @return 返回当前用户对该实体的点赞状态
     */
    public int getUserLikeStatus(int userId, int entityType, int entityId){
        String key = RedisUtils.getLikeKey(entityType, entityId);
        Boolean isMember = redisTemplate.opsForSet().isMember(key, userId);
        if(isMember)
            return CommunityConstant.LIKE_COMMON;
        else
            return CommunityConstant.LIKE_LIKED;
    }

    public int getUserTotalLike(int userId){
        String key = RedisUtils.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(key);
        return count == null ? 0 : count;
    }

}

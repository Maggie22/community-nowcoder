package com.nowcoder.community.service;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserService userService;

    @Autowired
    DiscussPostService discussPostService;

    public void follow(int userId, int entityType, int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisUtils.getFollowerKey(entityType, entityId); // 实体的关注者
                String followeeKey = RedisUtils.getFolloweeKey(userId, entityType); // 用户关注的

                operations.multi();

                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    public void unfollow(int userId, int entityType, int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisUtils.getFollowerKey(entityType, entityId); // 实体的关注者
                String followeeKey = RedisUtils.getFolloweeKey(userId, entityType); // 用户关注的

                operations.multi();

                operations.opsForZSet().remove(followerKey, userId);
                operations.opsForZSet().remove(followeeKey, entityId);

                return operations.exec();
            }
        });
    }

    public long getTotalFollower(int entityType, int entityId){
        String followerKey = RedisUtils.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    public long getTotalFollowee(int userId, int entityType){
        String followeeKey = RedisUtils.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    /**
     *
     * @return 返回用户对实体的关注状态
     */
    public boolean getFollowStatus(int userId, int entityType, int entityId){
        String followeeKey = RedisUtils.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    /**
     *
     * @return 返回用户对用户的关注状态
     */
    public boolean getFollowStatus(int userId, int targetUserId){
        return getFollowStatus(userId, CommunityConstant.TYPE_USER, targetUserId);
    }

    public List<Map<String, Object>> getFollowerList(int entityType, int entityId, int offset, int limit){
        String key = RedisUtils.getFollowerKey(entityType, entityId);
        Set<Integer> userIdList = redisTemplate.opsForZSet().reverseRange(key, offset, limit+offset-1);

        List<Map<String, Object>> followerList = new ArrayList<>();
        if(userIdList != null){
            for(int id: userIdList){
                Map<String, Object> map = new HashMap<>();
                User user = userService.findUserById(id);
                map.put("user", user);
                Double score = redisTemplate.opsForZSet().score(key, user.getId());
                map.put("followTime", new Date(score.longValue()));
                followerList.add(map);
            }
        }
        return followerList;
    }

    public List<Map<String, Object>> getFolloweeList(int userId, int entityType, int offset, int limit){
        String key = RedisUtils.getFolloweeKey(userId, entityType);
        Set<Integer> entityIdList = redisTemplate.opsForZSet().reverseRange(key, offset, limit+offset-1);

        List<Map<String, Object>> followeeList = new ArrayList<>();
        if(entityIdList != null){
            switch (entityType){
                case CommunityConstant.TYPE_USER ->{
                    for(int id: entityIdList){
                        Map<String, Object> map = new HashMap<>();
                        User user = userService.findUserById(id);
                        map.put("user", user);
                        Double score = redisTemplate.opsForZSet().score(key, user.getId());
                        map.put("followTime", new Date(score.longValue()));
                        followeeList.add(map);
                    }
                }
                case CommunityConstant.TYPE_POST -> {
                    for(int id: entityIdList){
                        Map<String, Object> map = new HashMap<>();
                        DiscussPost post = discussPostService.findDiscussPostById(id);
                        map.put("post", post);
                        Double score = redisTemplate.opsForZSet().score(key, post.getId());
                        map.put("followTime", new Date(score.longValue()));
                        followeeList.add(map);
                    }
                }
            }
        }
        return followeeList;
    }

}

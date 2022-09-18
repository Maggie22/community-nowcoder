package com.nowcoder.community.utils;

public class RedisUtils {

    private static final String SPLIT = ":";
    private static final String LIKE_ENTITY_PREFIX = "community:like:entity";
    private static final String LIKE_USER_PREFIX = "community:like:user";
    private static final String FOLLOWER_PREFIX = "community:follower"; // 某个用户被谁关注了
    private static final String FOLLOWEE_PREFIX = "community:followee"; // 某个用户关注了谁
    private static final String KAPTCHA_PREFIX = "community:kaptcha";
    private static final String LOGINTICKET_PREFIX = "community:loginticket";
    private static final String USER_PREFIX = "community:user";
    private static final String UV_PREFIX = "community:uv";
    private static final String DAU_PREFIX = "community:dau";

    public static String getLikeKey(int entityType, int entityId){
        return LIKE_ENTITY_PREFIX + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getUserLikeKey(int userId){
        return LIKE_USER_PREFIX + userId;
    }

    // 某个实体拥有的关注者（注意可以不只是用户）
    // community:follower:entityType:entityId -> zset(userId, now)
    public static String getFollowerKey(int entityType, int entityId){
        return FOLLOWER_PREFIX + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户关注的实体
    //  community:followee:userId:entityType -> zset(entityId, now)
    public static String getFolloweeKey(int userId, int entityType){
        return FOLLOWEE_PREFIX + SPLIT + userId + SPLIT + entityType;
    }

    public static String getKaptchaKey(String owner){
        return KAPTCHA_PREFIX + SPLIT + owner;
    }

    public static String getLoginticketKey(String ticket){
        return LOGINTICKET_PREFIX + SPLIT + ticket;
    }

    public static String getUserKey(int userId){
        return USER_PREFIX + SPLIT + userId;
    }

    public static String getUVKey(String date){
        return UV_PREFIX + SPLIT + date;
    }

    public static String getUVKey(String startKey, String endDate){
        return UV_PREFIX + SPLIT + startKey + SPLIT + endDate;
    }

    public static String getDAUKey(String date){
        return DAU_PREFIX + SPLIT + date;
    }

    public static String getDAUKey(String startKey, String endKey){
        return DAU_PREFIX + SPLIT + startKey + SPLIT + endKey;
    }
}

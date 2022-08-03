package com.nowcoder.community.utils;

public interface CommunityConstant {
    // 实体类型
    int TYPE_POST = 1;
    int TYPE_COMMENT = 2;
    int TYPE_USER = 3;

    // 激活状态
    int ACTIVATE_SUCCESS = 0;
    int ACTIVATE_REPEAT = 1;
    int ACTIVATE_FAILURE = 2;

    // 用户状态与类型
    int COMMON = 0;
    int MANAGER = 1;
    int MODERATOR = 2;
    int INACTIVATED = 0;
    int ACTIVATED = 1;

    // 登录状态与超时
    int LOGIN = 0;
    int LOGOUT = 1;
    int REMEMBER_EXPIRED_SECONDS = 10 * 24 * 3600;
    int DEFAULT_EXPIRED_SECONDS = 12 * 3600;

    // 帖子状态
    int POST_TYPE_COMMON = 0;
    int POST_TYPE_TOP = 1;
    int POST_STATUS_COMMON = 0;
    int POST_STATUS_RECOMMEND = 1;
    int POST_STATUS_INVALIDATE = 2;

    // 评论类型
    int COMMENT_TYPE_POST = 1;
    int COMMENT_TYPE_COMMENT = 2;

    // 评论状态
    int COMMENT_STATUS_COMMON = 0;
    int COMMENT_STATUS_INVALIDATE = 1;

    // 私信状态
    int MESSAGE_UNREAD = 0;
    int MESSAGE_READ = 1;
    int MESSAGE_DELETE = 2;

    // 点赞状态
    int LIKE_COMMON = 0;
    int LIKE_LIKED = 1;
    int LIKE_UNLIKE = 2;

    // 系统通知类型
    String NOTICE_TYPE_COMMENT = "comment";
    String NOTICE_TYPE_LIKE = "like";
    String NOTICE_TYPE_FOLLOW = "follow";

    // 其他
    int ADMIN_ID = 1;
}

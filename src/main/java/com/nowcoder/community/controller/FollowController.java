package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtils;
import com.nowcoder.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
public class FollowController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FollowService followService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId){
        if(hostHolder.getUser() == null)
            return CommunityUtils.getJSONString(400, "请先登录");
        int userId = hostHolder.getUser().getId();
        if(entityType == CommunityConstant.TYPE_USER){
            if(userId == entityId)
                return CommunityUtils.getJSONString(100, "不能关注自己哦~");
        }

        followService.follow(userId, entityType, entityId);

        int targetUserId = -1;
        switch (entityType){
            case CommunityConstant.TYPE_POST -> {
                DiscussPost discussPost = discussPostService.findDiscussPostById(entityId);
                targetUserId = discussPost.getUserId();
            }
            case CommunityConstant.TYPE_USER -> {
                targetUserId = entityId;
            }
        }
        // 发送消息
        if(userId != targetUserId) {
            Event event = new Event()
                    .setTopic(CommunityConstant.NOTICE_TYPE_FOLLOW)
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setUserId(userId)
                    .setTargetUserId(targetUserId);

            if (entityType == CommunityConstant.TYPE_POST) {
                event.setData("postId", entityId);
            }

            eventProducer.sendMessage(event);
        }

        return CommunityUtils.getJSONString(0, "关注成功！");
    }

    @LoginRequired
    @RequestMapping(value = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        int userId = hostHolder.getUser().getId();
        if(entityType == CommunityConstant.TYPE_USER){
            if(userId == entityId)
                return CommunityUtils.getJSONString(100, "不能关注自己哦~");
        }
        followService.unfollow(userId, entityType, entityId);
        return CommunityUtils.getJSONString(0, "取消成功！");
    }

}

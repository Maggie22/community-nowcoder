package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
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

    @RequestMapping(value = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int targetUserId){
        if(hostHolder.getUser() == null)
            return CommunityUtils.getJSONString(400, "请先登录");
        int userId = hostHolder.getUser().getId();
        if(userId == targetUserId)
            return CommunityUtils.getJSONString(100, "不能关注自己哦~");
        followService.follow(userId, CommunityConstant.TYPE_USER, targetUserId);
        return CommunityUtils.getJSONString(0, "关注成功！");
    }

    @LoginRequired
    @RequestMapping(value = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int targetUserId){
        int userId = hostHolder.getUser().getId();
        if(userId == targetUserId)
            return CommunityUtils.getJSONString(100, "不能关注自己哦~");
        followService.unfollow(userId, CommunityConstant.TYPE_USER, targetUserId);
        return CommunityUtils.getJSONString(0, "取消成功！");
    }

}

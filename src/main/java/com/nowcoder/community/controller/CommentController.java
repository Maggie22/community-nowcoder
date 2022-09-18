package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtils;
import com.nowcoder.community.utils.HostHolder;
import com.nowcoder.community.utils.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(value = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, int targetUserId, Comment comment, Model model) {
        if (StringUtils.isBlank(comment.getContent())) {
            model.addAttribute("commentMsg", "回复内容不能为空！");
            return "redirect:/discuss/detail/" + discussPostId;
        }
        if (hostHolder.getUser() == null) {
            return "redirect:/login";
        }
        // 补充信息
        comment.setCreateTime(new Date());
        comment.setStatus(CommunityConstant.COMMENT_STATUS_COMMON);
        comment.setUserId(hostHolder.getUser().getId());

        // 插入评论
        commentService.insertComment(comment);

        // 发送通知
        if (targetUserId != hostHolder.getUser().getId()) {
            // 不是本人时才发送通知
            Event event = new Event()
                    .setTopic(CommunityConstant.NOTICE_TYPE_COMMENT)
                    .setEntityType(comment.getEntityType())
                    .setEntityId(comment.getEntityId())
                    .setUserId(comment.getUserId())
                    .setData("postId", discussPostId)
                    .setTargetUserId(targetUserId);
            eventProducer.sendMessage(event);
        }


        if (comment.getEntityType() == CommunityConstant.TYPE_POST) {
            // 修改es数据库中帖子的评论数量
            Event event = new Event()
                    .setTopic(CommunityConstant.NOTICE_TYPE_POST)
                    .setEntityType(comment.getEntityType())
                    .setEntityId(comment.getEntityId())
                    .setUserId(comment.getUserId())
                    .setData("messageType", "insert");
            eventProducer.sendMessage(event);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}

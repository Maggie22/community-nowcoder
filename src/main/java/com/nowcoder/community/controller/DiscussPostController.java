package com.nowcoder.community.controller;

import com.nowcoder.community.entity.*;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtils;
import com.nowcoder.community.utils.HostHolder;
import com.nowcoder.community.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        if(hostHolder.getUser() == null)
            return CommunityUtils.getJSONString(405, "您还没有登录哦~");
        if(StringUtils.isBlank(title))
            return CommunityUtils.getJSONString(400, "标题不能为空");
        if(StringUtils.isBlank(content))
            return CommunityUtils.getJSONString(400, "内容不能为空");

        DiscussPost post = new DiscussPost();
        post.setUserId(hostHolder.getUser().getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        post.setScore(0.);
        post.setStatus(CommunityConstant.POST_STATUS_COMMON);
        post.setCommentCount(0);
        post.setType(CommunityConstant.POST_TYPE_COMMON);
        discussPostService.addDiscussPost(post);

        return CommunityUtils.getJSONString(0, "发布成功！");
    }

    @RequestMapping(value = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPostDetail(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("post", post);
        model.addAttribute("user", user);

        long likeCountPost = likeService.getTotalLike(CommunityConstant.TYPE_POST, post.getId());
        model.addAttribute("likeCount", likeCountPost);
        model.addAttribute("likeStatus", likeService.getUserLikeStatus(CommunityConstant.TYPE_POST, CommunityConstant.TYPE_POST, post.getId()));

        // 处理评论
        List<Map<String, Object>> commentVOList = new ArrayList<>();
        // 读帖子的所有评论
        page.setPath("/discuss/detail/" + discussPostId);
        page.setTotalRows(post.getCommentCount());

        List<Comment> commentList = commentService.selectCommentByEntity(CommunityConstant.COMMENT_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        if(commentList!=null){
            for(Comment comment: commentList){
                Map<String, Object> map = new HashMap<>();
                map.put("comment", comment);
                map.put("user", userService.findUserById(comment.getUserId()));
                map.put("replyCount", commentService.selectCount(CommunityConstant.COMMENT_TYPE_COMMENT, comment.getId()));
                long likeCountComment = likeService.getTotalLike(CommunityConstant.TYPE_COMMENT, comment.getId());
                map.put("likeCount", likeCountComment);
                // 处理评论的回复
                List<Comment> replyList = commentService.selectCommentByEntity(CommunityConstant.COMMENT_TYPE_COMMENT, comment.getId(), -1, 0);
                List<Map<String, Object>> replyVOList = new ArrayList<>();
                if(replyList!=null){
                    for(Comment reply: replyList){
                        Map<String, Object> replyMap = new HashMap<>();
                        replyMap.put("comment", reply);
                        replyMap.put("user", userService.findUserById(reply.getUserId()));
                        User target = reply.getTargetId()==null ? null : userService.findUserById(reply.getTargetId());
                        replyMap.put("target", target);
                        long likeCountReply = likeService.getTotalLike(CommunityConstant.TYPE_COMMENT, reply.getId());
                        replyMap.put("likeCount", likeCountReply);
                        replyVOList.add(replyMap);
                    }
                }
                map.put("replyList", replyVOList);
                commentVOList.add(map);
            }

        }

        model.addAttribute("commentList", commentVOList);
        return "/site/discuss-detail";
    }

    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String setLikeStatus(int entityType, int entityId, int targetUserId, int discussPostId){
        String key = RedisUtils.getLikeKey(entityType, entityId);
        int userId = hostHolder.getUser().getId();
        likeService.setLikeStatus(userId, entityType, entityId, targetUserId);

        // 状态
        int likeStatus = likeService.getUserLikeStatus(userId, entityType, entityId);
        // 点赞统计
        long likeCount = likeService.getTotalLike(entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeStatus", likeStatus);
        map.put("likeCount", likeCount);

        // 发送通知
        if(userId != targetUserId) {
            Event event = new Event()
                    .setTopic(CommunityConstant.NOTICE_TYPE_LIKE)
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setUserId(userId)
                    .setData("postId", discussPostId);

            if (entityType == CommunityConstant.TYPE_POST) {
                DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
                event.setTargetUserId(discussPost.getUserId());
            } else if (entityType == CommunityConstant.TYPE_COMMENT) {
                Comment comment = commentService.selectCommentById(entityId);
                event.setTargetUserId(comment.getUserId());
            }

            eventProducer.sendMessage(event);
        }


        return CommunityUtils.getJSONString(0, "成功", map);
    }
}

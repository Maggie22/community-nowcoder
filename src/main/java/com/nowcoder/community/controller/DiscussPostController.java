package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtils;
import com.nowcoder.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

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
}

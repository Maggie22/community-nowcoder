package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.utils.CommunityConstant;
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
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;


    @RequestMapping(value = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment, Model model){
        if(StringUtils.isBlank(comment.getContent())){
            model.addAttribute("commentMsg", "回复内容不能为空！");
            return "redirect:/discuss/detail/" + discussPostId;
        }
        if (hostHolder.getUser() == null){
            return "redirect:/login";
        }
        // 补充信息
        comment.setCreateTime(new Date());
        comment.setStatus(CommunityConstant.COMMENT_STATUS_COMMON);
        comment.setUserId(hostHolder.getUser().getId());

        // 插入评论
        int numRows = commentService.insertComment(comment);
        // 更新评论数量

        return "redirect:/discuss/detail/" + discussPostId;
    }
}

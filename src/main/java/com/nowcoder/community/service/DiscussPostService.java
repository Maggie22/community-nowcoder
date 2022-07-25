package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostsMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.utils.HostHolder;
import com.nowcoder.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    DiscussPostsMapper discussPostsMapper;

    @Autowired
    SensitiveFilter filter;

    @Autowired
    HostHolder hostHolder;

    public List<DiscussPost> findDiscussPostList(Integer userId, int offset, int limit){
        return discussPostsMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(Integer userId){
        return discussPostsMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post){
        if(post==null)
            throw new IllegalArgumentException("post参数不能为空！");

        // 处理html转义
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        // 敏感词处理
        post.setTitle(filter.filter(post.getTitle()));
        post.setContent(filter.filter(post.getContent()));

        // insert
        return discussPostsMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPostById(int id){
        return discussPostsMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount){
        return discussPostsMapper.updateCommentCount(id, commentCount);
    }
}

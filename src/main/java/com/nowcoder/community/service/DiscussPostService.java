package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostsMapper;
import com.nowcoder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    DiscussPostsMapper discussPostsMapper;

    public List<DiscussPost> findDiscussPostList(Integer userId, int offset, int limit){
        return discussPostsMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(Integer userId){
        return discussPostsMapper.selectDiscussPostRows(userId);
    }
}

package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostsMapper;
import com.nowcoder.community.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DiscussPostMapperTest {

    @Autowired
    DiscussPostsMapper discussPostsMapper;

    @Test
    public void testSelectDiscussPost(){
        List<DiscussPost> list = discussPostsMapper.selectDiscussPosts(null, 0, 10);
        for(DiscussPost discussPost: list)
            System.out.println(discussPost);
    }

    @Test
    public void testSelectRows(){
        int res = discussPostsMapper.selectDiscussPostRows(null);
        System.out.println(res);
    }
}

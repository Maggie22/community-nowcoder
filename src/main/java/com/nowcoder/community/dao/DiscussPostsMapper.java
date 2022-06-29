package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostsMapper {

    List<DiscussPost> selectDiscussPosts(@Param("userId") Integer userId, @Param("offset") int offset, @Param("limit") int limit);   // userId为了以后功能（查询单个用户的文章列)(动态sql实现

    int selectDiscussPostRows(Integer userId);

}

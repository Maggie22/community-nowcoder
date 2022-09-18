package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Mapper
public interface DiscussPostsMapper {

    List<DiscussPost> selectDiscussPosts(@Param("userId") Integer userId, @Param("offset") int offset, @Param("limit") int limit);   // userId为了以后功能（查询单个用户的文章列)(动态sql实现

    int selectDiscussPostRows(Integer userId);

    int insertDiscussPost(DiscussPost post);

    DiscussPost selectDiscussPostById(Integer postId);

    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    int updatePostStatus(@Param("id") int id, @Param("status") int status);

    int updatePostType(@Param("id") int id, @Param("type") int type);

    int selectUserIdByPostId(int id);
}

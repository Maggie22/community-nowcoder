package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentByEntity(@Param("entityType") int entityType, @Param("entityId") int entityId,
                                        @Param("offset") int offset, @Param("limit") int limit);

    List<Comment> selectCommentList(@Param("userId")int userId, @Param("entityType") int entityType, @Param("offset") int offset, @Param("limit") int limit);

    int selectCountByUser(@Param("entityType") int entityType, @Param("userId") int userId);

    int selectCountByEntity(@Param("entityType") int entityType, @Param("entityId") int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);

}

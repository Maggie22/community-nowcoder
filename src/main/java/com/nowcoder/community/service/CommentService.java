package com.nowcoder.community.service;

import com.nowcoder.community.controller.DiscussPostController;
import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    SensitiveFilter sensitiveFilter;

    public List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit){
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    public int selectCount(int entityType, int entityId){
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int insertComment(Comment comment){
        if(comment == null)
            throw new IllegalArgumentException("评论不能为空！");
        // 敏感词处理
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rowNums = commentMapper.insertComment(comment);

        // 更新评论数量
        if(comment.getEntityType() == CommunityConstant.COMMENT_TYPE_POST){
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }

        return rowNums;
    }

}

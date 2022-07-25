package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {

    /**
     * 分页查询所有会话中的最新一条消息
     */
    List<Message> selectValidateConversation(@Param("userId") int userId,
                                             @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 查询该用户的会话总数
     * @param userId
     * @return
     */
    int selectCountConversation(@Param("userId") int userId);


    /**
     * 查询会话中的未读消息数量
     * @param conversationId
     * @param userId
     * @return
     */
    int selectUnreadCountMessage(@Param("conversationId") String conversationId, @Param("userId") int userId);


    /**
     * 查询某个会话下的所有消息
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectLetters(@Param("conversationId") String conversationId,
                                @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 查询某个会话下的消息数量
     * @param conversationId
     * @return
     */
    int selectCountLetters(String conversationId);

    int insertMessage(Message message);

    int updateUnreadMessages(@Param("idList") List<Integer> idList, @Param("userId") int userId);

    int deleteMessageById(int id);
}

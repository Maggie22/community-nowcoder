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

    /**
     * 查询未读通知数量，当topic为null时，查询所有类型的通知数量
     * @param userId
     * @return
     */
    int selectUnreadNoticeCount(@Param("userId") int userId, @Param("topic") String topic);

    /**
     * 查询最新通知
     * @param userId
     * @param topic
     * @return
     */
    Message selectLatestNotice(@Param("userId") int userId, @Param("topic") String topic);


    /**
     * 分页查询通知
     */
    List<Message> selectNoticeList(@Param("userId") int userId, @Param("topic") String topic,
                                   @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 查询通知总数
     */
    int selectNoticeCount(@Param("userId") int userId, @Param("topic") String topic);

}

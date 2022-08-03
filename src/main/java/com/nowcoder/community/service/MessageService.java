package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.HostHolder;
import com.nowcoder.community.utils.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Autowired
    HostHolder hostHolder;

    public List<Message> getConversationList(int userId, int offset, int limit){
        return messageMapper.selectValidateConversation(userId, offset, limit);
    }

    public int getConversationCount(int userId){
        return messageMapper.selectCountConversation(userId);
    }

    public List<Message> getLetters(String conversationId, int offset, int limit){
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    public int getCountUnreadMessage(String conversationId, int userId){
        return messageMapper.selectUnreadCountMessage(conversationId, userId);
    }

    public int getCountMessage(String conversationId){
        return messageMapper.selectCountLetters(conversationId);
    }

    public String addMessage(Message message){
        messageMapper.insertMessage(message);
        return null;
    }

    public int updateUnread(List<Integer> idList, int userId){
        return messageMapper.updateUnreadMessages(idList, userId);
    }

    public int deleteMessage(int id){
        return messageMapper.deleteMessageById(id);
    }

    public Message getLatestNotice(int userId, String topic){
        return messageMapper.selectLatestNotice(userId, topic);
    }

    public List<Message> getNoticeList(int userId, String topic, int offset, int limit){
        return messageMapper.selectNoticeList(userId, topic, offset, limit);
    }

    public int getUnreadNoticeCount(int userId, String topic){
        return messageMapper.selectUnreadNoticeCount(userId, topic);
    }

    public int getNoticeCount(int userId, String topic){
        return messageMapper.selectNoticeCount(userId, topic);
    }
}

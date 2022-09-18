package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticSearchService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.utils.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    MessageService messageService;

    @Autowired
    DiscussPostService postService;

    @Autowired
    ElasticSearchService searchService;

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @KafkaListener(topics = {"like", "follow", "comment"})
    public void handleMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            logger.error("消息内容为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            logger.error("消息内容为空");
            return;
        }

        Message message = new Message();
        message.setStatus(CommunityConstant.MESSAGE_UNREAD);
        message.setFromId(1);       // 发送者为系统
        message.setToId(event.getTargetUserId());
        message.setCreateTime(new Date());
        message.setConversationId(event.getTopic());

        Map<String, Object> content = new HashMap<>();
        content.put("entityType", event.getEntityType());   // 事件对应对象（评论、帖子、用户）的类型
        content.put("entityId", event.getEntityId());       // 事件对应对象（评论、帖子、用户）的id
        content.put("userId", event.getUserId());           // 触发该事件的用户

        if(!event.getData().isEmpty()){
            content.putAll(event.getData());
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    @KafkaListener(topics = {"discusspost"})
    public void handlePostMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            logger.error("消息内容为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            logger.error("消息内容为空");
            return;
        }
        Map<String, Object> data = event.getData();
        if(data.isEmpty()){
            logger.error("data内容为空");
            return;
        }
        String messageType = (String) data.get("messageType");
        if("insert".equals(messageType)) {
            DiscussPost post = postService.findDiscussPostById(event.getEntityId());
            searchService.insertDiscussPost(post);
        }
        else if ("delete".equals(messageType)){
            searchService.deleteDiscussPost(event.getEntityId());
        }
    }
}

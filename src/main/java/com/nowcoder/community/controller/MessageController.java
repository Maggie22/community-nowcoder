package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtils;
import com.nowcoder.community.utils.HostHolder;
import com.nowcoder.community.utils.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
@RequestMapping("/letter")
public class MessageController {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    SensitiveFilter sensitiveFilter;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String getConversationList(Model model, Page page){
        User user = hostHolder.getUser();
        // 分页设置
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setTotalRows(messageService.getConversationCount(user.getId()));

        // 消息体设置
        List<Map<String, Object>> conversationList = new ArrayList<>();
        List<Message> messages = messageService.getConversationList(user.getId(), page.getOffset(), page.getLimit());
        if(messages!=null){
            for (Message message: messages){
                Map<String, Object> map = new HashMap<>();
                User other = message.getFromId().equals(user.getId()) ?
                        userService.findUserById(message.getToId()) : userService.findUserById(message.getFromId());
                map.put("otherUser", other);
                map.put("message", message);
                map.put("unreadCount", messageService.getCountUnreadMessage(message.getConversationId(), user.getId()));
                map.put("totalCount", messageService.getCountMessage(message.getConversationId()));
                conversationList.add(map);
            }
        }

        model.addAttribute("messages", conversationList);
        System.out.println(messageService.getCountUnreadMessage(null, user.getId()));
        model.addAttribute("messageUnread", messageService.getCountUnreadMessage(null, user.getId()));
        return "/site/letter";
    }

    @RequestMapping(value = "/detail/{conversationId}", method = RequestMethod.GET)
    public String getDetailedConversation(@PathVariable("conversationId")String conversationId, Model model, Page page){
        // 分页设置
        page.setPath("/letter/detail/" + conversationId);
        page.setLimit(5);
        page.setTotalRows(messageService.getCountMessage(conversationId));

        // 当前用户
        User user = hostHolder.getUser();

        // 另一个用户
        int otherUserId = getOtherUserId(conversationId);
        User otherUser = userService.findUserById(otherUserId);

        // 私信列表
        List<Message> messages = messageService.getLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> messageList = new ArrayList<>();
        if(messages!=null){
            for(Message message: messages){
                Map<String, Object> map = new HashMap<>();
                map.put("message", message);
                map.put("fromUser", message.getFromId() == otherUserId ? otherUser : user);
                messageList.add(map);
            }
        }

        // 更新未读状态
        List<Integer> idList = getIds(messages);
        if(!idList.isEmpty()){
            messageService.updateUnread(idList, hostHolder.getUser().getId());
        }

        model.addAttribute("messageList", messageList);
        model.addAttribute("otherUser", otherUser);
        return "/site/letter-detail";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String sendMessage(String toUsername, String content){
        if(StringUtils.isBlank(toUsername))
            return CommunityUtils.getJSONString(1, "用户名不能为空");
        if(StringUtils.isBlank(content))
            return CommunityUtils.getJSONString(1, "内容不能为空");

        User user = userService.findUserByName(toUsername);
        if(user == null)
            return CommunityUtils.getJSONString(1, "用户名不存在");

        Message message = new Message();
        content = HtmlUtils.htmlEscape(content);
        content = sensitiveFilter.filter(content);
        message.setContent(content);
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(user.getId());
        message.setConversationId(getConversationId(user.getId(), hostHolder.getUser().getId()));
        message.setStatus(CommunityConstant.MESSAGE_UNREAD);
        message.setCreateTime(new Date());

        messageService.addMessage(message);
        return CommunityUtils.getJSONString(0, "发送成功！");
    }


    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String deleteMessage(int id){
        if(messageService.deleteMessage(id) > 0)
            return CommunityUtils.getJSONString(0, "删除成功！");
        return CommunityUtils.getJSONString(1, "删除失败！");
    }

    private int getOtherUserId(String conversationId){
        int first = Integer.parseInt(conversationId.split("_")[0]);
        int second = Integer.parseInt(conversationId.split("_")[1]);
        return first == hostHolder.getUser().getId() ? second : first;
    }

    private String getConversationId(int first, int second){
        if(first < second)
            return first + "_" + second;
        return second + "_" +first;
    }

    private List<Integer> getIds(List<Message> messageList){
        List<Integer> list = new ArrayList<>();
        if(messageList!=null){
            for(Message message: messageList){
                if(message.getStatus() == CommunityConstant.MESSAGE_UNREAD && message.getToId().equals(hostHolder.getUser().getId()))
                    list.add(message.getId());
            }
        }
        return list;
    }
}

package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtils;
import com.nowcoder.community.utils.MailClient;
import com.nowcoder.community.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    LoginTicketMapper loginTicketMapper;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    MailClient mailClient;

    @Value("${community.path.domain}")
    String domain;

    @Value("${server.servlet.context-path}")
    String contextPath;

    private static final String USERNAME_MSG = "usernameMsg";
    private static final String PASSWORD_MSG = "passwordMsg";

    public Map<String, String> login(String username, String password, long expired){
        Map<String, String> map = new HashMap<>();

        if(StringUtils.isBlank(username)){
            map.put(USERNAME_MSG, "用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put(PASSWORD_MSG, "密码不能为空");
            return map;
        }

        User user = userMapper.selectByName(username);

        if(user == null){
            map.put(USERNAME_MSG, "用户不存在");
            return map;
        }

        if(user.getStatus() == CommunityConstant.INACTIVATED){
            map.put(USERNAME_MSG, "用户未激活");
            return map;
        }

        if(!user.getPassword().equals(CommunityUtils.md5(password+user.getSalt()))){
            map.put(PASSWORD_MSG, "密码错误");
            return map;
        }

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket(CommunityUtils.generateUUID());
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(CommunityConstant.LOGIN);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expired * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket){
        loginTicketMapper.updateStatusByTicket(ticket, CommunityConstant.LOGOUT);
    }

    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    public Map<String, String> register(User user){
        if(user == null){
            throw new IllegalArgumentException("User不能为空！");
        }

        // 输入是否符合条件
        Map<String, String> map = new HashMap<>();
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }
        if(!UserUtils.isLegalEmail(user.getEmail())){
            map.put("emailMsg", "邮箱格式错误");
            return map;
        }
        String passwordMsg = UserUtils.isLegalPassword(user.getPassword());
        if (!"".equals(passwordMsg)){
            map.put("passwordMsg", passwordMsg);
            return map;
        }

        // 用户名或密码是否已经存在
        User u = userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg", "用户名已存在");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg", "邮箱已存在");
            return map;
        }

        // 密码加密
        String salt = CommunityUtils.generateUUID().substring(0, 5);
        user.setSalt(salt);
        user.setPassword(CommunityUtils.md5(user.getPassword()+salt));

        // 其他信息设置
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setStatus(CommunityConstant.INACTIVATED);
        user.setType(CommunityConstant.COMMON);
        user.setCreateTime(new Date());
        user.setActivationCode(CommunityUtils.generateUUID());
        userMapper.insert(user);

        // 发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + contextPath + "/verify-email?uid=" +user.getId() + "&code=" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendEmail(user.getEmail(), "【牛客测试】验证电子邮箱", content);

        return map;
    }

    public int activate(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user == null)
            return CommunityConstant.ACTIVATE_FAILURE;
        int status = user.getStatus();
        if(status==CommunityConstant.ACTIVATED){
            // 已激活
            return CommunityConstant.ACTIVATE_REPEAT;
        }
        else{
            // 未激活
            if(code.equals(user.getActivationCode())){
                userMapper.updateStatus(userId, CommunityConstant.ACTIVATED);
                return CommunityConstant.ACTIVATE_SUCCESS;
            }
            else{
                return CommunityConstant.ACTIVATE_FAILURE;
            }
        }
    }

    public User findUserById(int userId){
        return userMapper.selectById(userId);
    }

    public int updateHeadUrl(int userId, String headerUrl){
        return userMapper.updateHeaderUrl(userId, headerUrl);
    }

    public Map<String, String> updatePassword(int userId, String oldPasswordInput, String newPassword){
        HashMap<String, String> map = new HashMap<>();
        User user = userMapper.selectById(userId);
        oldPasswordInput = CommunityUtils.md5(oldPasswordInput + user.getSalt());
        if(StringUtils.isBlank(oldPasswordInput) || !oldPasswordInput.equals(user.getPassword())){
            map.put("oldPasswordMsg", "原密码错误");
            return map;
        }

        String msg = UserUtils.isLegalPassword(newPassword);
        if(!"".equals(msg)){
            map.put("newPasswordMsg", msg);
            return map;
        }

        userMapper.updatePassword(userId, CommunityUtils.md5(newPassword + user.getSalt()));

        return map;
    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }
}

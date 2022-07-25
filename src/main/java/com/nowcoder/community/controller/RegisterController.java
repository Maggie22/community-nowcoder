package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class RegisterController {

    @RequestMapping(path="/register", method=RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @Autowired
    UserService userService;

    @RequestMapping(path="/register", method=RequestMethod.POST)
    public String registerUser(Model model, User user){
        Map<String, String> map = userService.register(user);
        if(map == null || map.isEmpty()){
            model.addAttribute("msg", "注册成功！我们已经向您邮箱发送了一封激活邮件，请尽快激活~");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }
        else{
            model.addAllAttributes(map);
            return "/site/register";
        }
    }

    @RequestMapping(path = "/verify-email", method = RequestMethod.GET)
    public String activate(Model model, int uid, String code){
        int msgCode = userService.activate(uid, code);
        if(msgCode == CommunityConstant.ACTIVATE_SUCCESS){
            model.addAttribute("msg", "激活成功，请登录！");
            model.addAttribute("target", "/login");
        }
        else if(msgCode == CommunityConstant.ACTIVATE_REPEAT){
            model.addAttribute("msg", "您已成功激活邮箱，请勿重复操作~");
            model.addAttribute("target", "/login");
        }
        else if(msgCode == CommunityConstant.ACTIVATE_FAILURE){
            model.addAttribute("msg", "激活码无效");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

}

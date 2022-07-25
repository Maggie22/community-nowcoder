package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.util.Map;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Value("${server.servlet.context-path}")
    String contextPath;

    @Autowired
    private Producer kaptchaProducer;

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String verifyCodeInput, boolean remember,
                        HttpSession session, Model model, HttpServletResponse response){
        // 先判断验证码~
        String verifyCode = (String) session.getAttribute("verifyCode");
        if(StringUtils.isBlank(verifyCode) || StringUtils.isBlank(verifyCodeInput)
                || !verifyCode.equalsIgnoreCase(verifyCodeInput)){
            model.addAttribute("verifyCodeMsg", "验证码错误");
            return "/site/login";
        }

        int expired = remember ? CommunityConstant.REMEMBER_EXPIRED_SECONDS : CommunityConstant.DEFAULT_EXPIRED_SECONDS;
        // 再登录！
        Map<String, String> map = userService.login(username, password, expired);

        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket"));
            cookie.setPath(contextPath);
            cookie.setMaxAge(expired);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAllAttributes(map);
            return "/site/login";
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/index";
    }

    @RequestMapping(value = "/verify-image", method = RequestMethod.GET)
    public void getVerifyImage(HttpServletResponse response, HttpSession session){
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        session.setAttribute("verifyCode", text);

        response.setContentType("image/png");
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            ImageIO.write(image, "png", outputStream);
        }catch (Exception e){
            logger.error("响应验证码错误：" + e.getMessage());
        }

    }
}

package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtils;
import com.nowcoder.community.utils.HostHolder;
import com.nowcoder.community.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage(){
        if(hostHolder.getUser()!=null)
            return "redirect:/index";
        return "/site/login";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(@CookieValue(value = "kaptchaOwner", required = false) String kaptchaOwner, String username, String password, String verifyCodeInput, boolean remember,
                        Model model, HttpServletResponse response){
        // 先判断验证码~
        if(StringUtils.isBlank(kaptchaOwner)){
            // 验证码已失效
            model.addAttribute("verifyCodeMsg", "验证码失效，请重试");
            return "/site/login";
        }

        String kaptchaKey = RedisUtils.getKaptchaKey(kaptchaOwner);
        String verifyCode = (String) redisTemplate.opsForValue().get(kaptchaKey);

        if(StringUtils.isBlank(verifyCode)){
            // 验证码已失效
            model.addAttribute("verifyCodeMsg", "验证码失效，请重试");
            return "/site/login";
        }

        if(StringUtils.isBlank(verifyCodeInput) || !verifyCode.equalsIgnoreCase(verifyCodeInput)) {
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

//        session.setAttribute("verifyCode", text);
        // 给用户一个用来判断验证码的凭证
        String kaptchaOwner = CommunityUtils.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(120);   // 2分钟有效
        cookie.setPath(contextPath);
        response.addCookie(cookie);

        // 保存验证码到redis
        String kaptchaKey = RedisUtils.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(kaptchaKey, text, 120, TimeUnit.SECONDS);

        response.setContentType("image/png");
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            ImageIO.write(image, "png", outputStream);
        }catch (Exception e){
            logger.error("响应验证码错误：" + e.getMessage());
        }

    }
}

package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityUtils;
import com.nowcoder.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Value("${community.path.uploadHeader}")
    String uploadHeaderPath;

    @Value("${community.path.domain}")
    String domain;

    @Value("${server.servlet.context-path}")
    String contextPath;


    private final String HEADER_URL_MSG = "headerUrlMsg";

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @LoginRequired
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadHeaderUrl(MultipartFile headerImage, Model model){

        // 获取文件
        if(headerImage.isEmpty()){
            model.addAttribute(HEADER_URL_MSG, "请上传文件！");
            return "/site/setting";
        }

        // 文件保存名和路径
        String originalFilename = headerImage.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")+1);

        Set<String> allowSuffix = new HashSet<>(Arrays.asList("png", "jpg", "jpeg", "gif", "bmp"));

        if(StringUtils.isBlank(suffix) || !allowSuffix.contains(suffix)){
            model.addAttribute(HEADER_URL_MSG, "请上传图片格式的文件！");
            return "/site/setting";
        }

        String newFilename = CommunityUtils.generateUUID() + "." + suffix;
        String filePath = uploadHeaderPath + "/" + newFilename;

        try {
            headerImage.transferTo(new File(filePath));
        } catch (IOException e) {
            logger.error("服务器保存图像出错：" + e.getMessage());
        }

        // 修改user的headerUrl信息，因为是本机作为图像存储服务器，通过项目来访问
        String fileUrl = domain + contextPath + "/user/header/" + newFilename;
        userService.updateHeadUrl(hostHolder.getUser().getId(), fileUrl);

        return "redirect:/user/setting";
    }

    @RequestMapping(value = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(HttpServletResponse response, @PathVariable("fileName") String fileName){
        String suffix = fileName.substring(fileName.lastIndexOf(".") +1);
        String filePath = uploadHeaderPath + "/" + fileName;
        response.setContentType("image/" + suffix);
        try(
                OutputStream os = response.getOutputStream();
                FileInputStream file = new FileInputStream(filePath);
                )
        {
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len=file.read(buffer))!=-1){
                os.write(buffer, 0, len);
            }
        }catch (Exception e){
            logger.error("头像读取错误" + e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(value = "/update-password", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, Model model){
        Map<String, String> map = userService.updatePassword(hostHolder.getUser().getId(), oldPassword, newPassword);

        if (map != null && !map.isEmpty()) {
            model.addAllAttributes(map);
            model.addAttribute("success", false);
        }
        else{
            model.addAttribute("success", true);
        }
        return "/site/setting";
    }
}

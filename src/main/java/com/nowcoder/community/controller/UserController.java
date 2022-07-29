package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.*;
import com.nowcoder.community.utils.CommunityConstant;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Value("${community.path.uploadHeader}")
    private String uploadHeaderPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

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

    @RequestMapping("/profile/{userId}")
    public String getProfilePage(Model model, @PathVariable("userId") int userId){
        User user = userService.findUserById(userId);
        if (user == null)
            return "/error/404";
        int likeCount = likeService.getUserTotalLike(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("likeCount", likeCount);

        long followerCount = followService.getTotalFollower(CommunityConstant.TYPE_USER, userId);
        long followeeCount = followService.getTotalFollowee(userId, CommunityConstant.TYPE_USER);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followeeCount", followeeCount);

        if(hostHolder.getUser() == null) {
            model.addAttribute("followStatus", false);
            model.addAttribute("isSelf", false);
        }
        else{
            if(hostHolder.getUser().getId() == userId){
                // 是自己
                model.addAttribute("isSelf", true);
            }
            else {
                model.addAttribute("isSelf", false);
                boolean status = followService.getFollowStatus(hostHolder.getUser().getId(), CommunityConstant.TYPE_USER, userId);
                model.addAttribute("followStatus", status);
            }
        }
        return "/site/profile";
    }

    @RequestMapping(value = "/follower/{userId}", method = RequestMethod.GET)
    public String getFollowerList(Model model, @PathVariable("userId") int userId, Page page){
        User user = userService.findUserById(userId);
        if(user == null){
            return "404";
        }
        model.addAttribute("user", user);

        page.setPath("/user/follower/" + userId);
        page.setTotalRows((int) followService.getTotalFollower(CommunityConstant.TYPE_USER, userId));
        page.setLimit(5);

        List<Map<String, Object>> followerVOList = followService.getFollowerList(CommunityConstant.TYPE_USER, userId, page.getOffset(), page.getLimit());
        if(hostHolder.getUser()!=null){
            int loginUserId = hostHolder.getUser().getId();
            for(Map<String, Object> map:followerVOList){
                User targetUser = (User) map.get("user");
                boolean isFollower = followService.getFollowStatus(loginUserId, targetUser.getId());
                map.put("isFollower", isFollower);
                if(targetUser.getId() == loginUserId)
                    map.put("isSelf", true);
                else
                    map.put("isSelf", false);
            }
        }
        else {
            for(Map<String, Object> map:followerVOList){
                map.put("isFollower", false);
                map.put("isSelf", false);
            }
        }

        model.addAttribute("followerList", followerVOList);
        return "/site/follower";
    }

    @RequestMapping(value = "/followee/{userId}", method = RequestMethod.GET)
    public String getFolloweeUserList(Model model, @PathVariable("userId") int userId, Page page){
        User user = userService.findUserById(userId);
        if(user == null){
            return "404";
        }
        model.addAttribute("user", user);

        page.setPath("/user/followee?" + userId);
        page.setTotalRows((int) followService.getTotalFollowee(userId, CommunityConstant.TYPE_USER));
        page.setLimit(5);

        List<Map<String, Object>> followeeVOList = followService.getFolloweeList(userId, CommunityConstant.TYPE_USER, page.getOffset(), page.getLimit());
        if(hostHolder.getUser()!=null){
            int loginUserId = hostHolder.getUser().getId();
            for(Map<String, Object> map: followeeVOList){
                User targetUser = (User) map.get("user");
                boolean isFollower = followService.getFollowStatus(loginUserId, targetUser.getId());
                map.put("isFollower", isFollower);
                if(targetUser.getId() == loginUserId)
                    map.put("isSelf", true);
                else
                    map.put("isSelf", false);
            }
        }
        else {
            for(Map<String, Object> map: followeeVOList){
                map.put("isFollower", false);
                map.put("isSelf", false);
            }
        }

        model.addAttribute("followeeList", followeeVOList);
        return "/site/followee";
    }

    @RequestMapping(value = "/discuss/{userId}", method = RequestMethod.GET)
    public String getUserDiscussPostList(Model model, @PathVariable("userId") int userId, Page page){
        User user = userService.findUserById(userId);
        if(user == null)
            return "404";
        model.addAttribute("user", user);
        // 分页设置
        page.setTotalRows(discussPostService.findDiscussPostRows(userId));
        page.setLimit(5);
        page.setPath("/user/discuss/" + userId);
        // 查找帖子列表
        List<DiscussPost> discussPostList = discussPostService.findDiscussPostList(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPostVOList = new ArrayList<>();
        for(DiscussPost discussPost: discussPostList){
            Map<String, Object> map = new HashMap<>();
            map.put("discussPost", discussPost);
            map.put("likeCount", likeService.getTotalLike(CommunityConstant.TYPE_POST, discussPost.getId()));
            discussPostVOList.add(map);
        }

        model.addAttribute("discussPostVOList", discussPostVOList);
        return "/site/my-post";
    }

    @RequestMapping(value = "/reply/{userId}", method = RequestMethod.GET)
    public String getUserReplyList(Model model, @PathVariable("userId") int userId, Page page){
        User user = userService.findUserById(userId);
        if(user == null)
            return "404";
        model.addAttribute("user", user);

        // 分页设置
        page.setTotalRows(commentService.selectCountForPostByUser(userId));
        page.setLimit(10);
        page.setPath("/user/reply/" + userId);

        // 查找回复列表
        List<Comment> commentList = commentService.selectCommentListForPost(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVOList = new ArrayList<>();
        for(Comment comment: commentList){
            Map<String, Object> map = new HashMap<>();
            map.put("comment", comment);
            DiscussPost post = discussPostService.findDiscussPostById(comment.getEntityId());
            map.put("postTitle", post.getTitle());
            map.put("postId", post.getId());
            commentVOList.add(map);
        }
        model.addAttribute("commentList", commentVOList);
        return "/site/my-reply";
    }
}

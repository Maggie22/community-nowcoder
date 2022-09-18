package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.ElasticSearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ElasticSearchController {

    @Autowired
    private ElasticSearchService searchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping("/search")
    public String search(String query, Page page, Model model) throws IOException {
        Map<String, Object> result = searchService.searchDiscussPost(query, page.getCurrent(), page.getLimit());

        List<DiscussPost> discussList = (List<DiscussPost>) result.get("list");
        List<Map<String, Object>> postVOList = new ArrayList<>();

        for(DiscussPost post: discussList){
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);
            map.put("user", userService.findUserById(post.getUserId()));
            map.put("likeCount", likeService.getTotalLike(CommunityConstant.TYPE_POST, post.getId()));
            postVOList.add(map);
        }

        model.addAttribute("discussList", postVOList);
        model.addAttribute("query", query);

        page.setPath("/search?query=" + query);
        page.setTotalRows((Integer) result.get("total"));
        return "/site/search";
    }
}

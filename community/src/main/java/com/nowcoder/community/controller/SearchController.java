package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticSearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class SearchController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/search")
    public String search(String keyword, Page page, Model model) {
        try {
            // 搜索帖子
            List<DiscussPost> discussPosts = elasticSearchService.search(keyword, page.getCurrent() - 1, page.getLimit());
            List<Map<String, Object>> result = new ArrayList<>();
            if (discussPosts != null) {
                for (DiscussPost post : discussPosts) {
                    Map<String, Object> map = new HashMap<>();
                    // 帖子
                    map.put("post", post);
                    // 作者
                    map.put("user", userService.findByUserId(post.getUserId()));
                    // 点赞数量
                    map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                    result.add(map);
                }
            }
            model.addAttribute("discussPosts", result);
            model.addAttribute("keyword", keyword);
            // 分页信息
            page.setPath("/search?keyword=" + keyword);
            page.setRows(discussPosts == null ? 0 : discussPosts.size());
            return "/site/search";
        } catch (IOException e) {
            log.error("搜索失败: " + e.getMessage());
            return "redirect:/error";
        }
    }

    @GetMapping("/admin/import/{secret}")
    @ResponseBody
    public String importData(@PathVariable("secret") String secret) {
        if (secret.equalsIgnoreCase("system")) {
            List<DiscussPost> posts = discussPostService.findAll();
            for (DiscussPost post : posts) {
                elasticSearchService.saveDiscussPost(post);
            }
            return CommunityUtil.getJSONString(0, "操作成功");
        }
        return CommunityUtil.getJSONString(1, "操作失败，密钥错误");
    }
}

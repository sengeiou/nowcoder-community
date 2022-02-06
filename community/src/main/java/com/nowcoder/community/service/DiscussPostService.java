package com.nowcoder.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.util.SensitiveFilter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    @PostConstruct
    public void init() {
        // 初始化帖子列表
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(@NonNull String key) throws Exception {
                        String[] params = key.split(":");
                        int offset = Integer.parseInt(params[0]);
                        int limit = Integer.parseInt(params[1]);
                        // TODO: 二级缓存 redis -> mysql
                        log.debug("load post list from db.");
                        return discussPostMapper.getDiscussPostList(0, offset, limit, 1);
                    }
                });
        // 初识化帖子总数
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Long, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull Long key) throws Exception {
                        // TODO: 二级缓存 redis -> mysql
                        log.debug("load post rows from db.");
                        return discussPostMapper.getDiscussPostCount(key);
                    }
                });
    }

    // 帖子列表的缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    // 帖子总数缓存
    private LoadingCache<Long, Integer> postRowsCache;

    public List<DiscussPost> findDiscussPosts(long userId, int offset, int limit, int orderMode) {
        if (userId == 0 && orderMode == 1) {
            return postListCache.get(offset + ":" + limit);
        }
        log.debug("load post list from db.");
        return discussPostMapper.getDiscussPostList(userId, offset, limit, orderMode);
    }

    public int findDiscussPostRows(long userId) {
        if (userId == 0) {
            return postRowsCache.get(userId);
        }
        log.debug("load post rows from db.");
        return discussPostMapper.getDiscussPostCount(userId);
    }

    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        // 转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        // 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));
        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPostById(long id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(long id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    public List<DiscussPost> findAll() {
        return discussPostMapper.selectAll();
    }

    public int updateType(long id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    public int updateStatus(long id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    public int updateScore(long id, double score) {
        return discussPostMapper.updateScore(id, score);
    }
}

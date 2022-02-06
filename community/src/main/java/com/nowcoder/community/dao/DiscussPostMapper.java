package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> getDiscussPostList(@Param("userId") long userId, @Param("offset") int offset, @Param("limit") int limit, @Param("orderMode") int orderMode);
    int getDiscussPostCount(@Param("userId") long userId);
    int insertDiscussPost(DiscussPost discussPost);
    DiscussPost selectDiscussPostById(@Param("id") long id);
    int updateCommentCount(@Param("id") long id, @Param("commentCount") int commentCount);
    List<DiscussPost> selectAll();
    int updateType(long id, int type);
    int updateStatus(long id, int status);
    int updateScore(long id, double score);
}

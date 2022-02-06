package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    // 查询当前用户的会话列表
    List<Message> selectConversations(@Param("userId") long userId, @Param("offset") int offset, @Param("limit") int limit);
    // 查询当前用户的会话数量
    int selectConversationCount(long userId);
    // 查询某个会话所包含的私信列表
    List<Message> selectLetters(@Param("conversationId") String conversationId, @Param("offset") int offset, @Param("limit") int limit);
    // 查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);
    // 查询未读私信的数量
    int selectLetterUnreadCount(@Param("userId") long userId, @Param("conversationId") String conversationId);
    // 新增消息
    int insertMessage(Message message);
    // 修改消息状态
    int updateStatus(List<Long> ids, int status);
    // 查询某个主题下最新的通知
    Message selectLatestNotice(long userId, String topic);
    // 查询某个主题所包含的通知数量
    int selectNoticeCount(long userId, String topic);
    // 查询未读的通知数量
    int selectNoticeUnreadCount(@Param("userId") long userId, @Param("topic") String topic);
    // 查询某个主题所包含的通知列表
    List<Message> selectNotices(long userId, String topic, int offset, int limit);
}

package com.thfh.repository;

import com.thfh.model.ChatMessage;
import com.thfh.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    // 查询两个用户之间的聊天记录
    @Query("SELECT m FROM ChatMessage m WHERE " +
           "(m.sender = :user1 AND m.receiver = :user2) OR " +
           "(m.sender = :user2 AND m.receiver = :user1) " +
           "ORDER BY m.sentTime ASC")
    List<ChatMessage> findMessagesBetweenUsers(User user1, User user2);
    
    // 查询用户的所有未读消息
    List<ChatMessage> findByReceiverAndReadFalseOrderBySentTimeDesc(User receiver);
    
    // 查询用户的最新聊天会话列表（去重）
    @Query(value = "SELECT m.* FROM chat_messages m " +
           "INNER JOIN (" +
           "  SELECT " +
           "    CASE WHEN sender_id = :userId THEN receiver_id ELSE sender_id END AS other_user_id, " +
           "    MAX(sent_time) as max_time " +
           "  FROM chat_messages " +
           "  WHERE sender_id = :userId OR receiver_id = :userId " +
           "  GROUP BY other_user_id" +
           ") latest ON " +
           "((m.sender_id = :userId AND m.receiver_id = latest.other_user_id) OR " +
           "(m.receiver_id = :userId AND m.sender_id = latest.other_user_id)) " +
           "AND m.sent_time = latest.max_time " +
           "ORDER BY m.sent_time DESC", 
           nativeQuery = true)
    List<ChatMessage> findRecentChatsByUser(Long userId);
    
    // 统计某用户未读消息数量
    Long countByReceiverAndReadFalse(User receiver);
} 
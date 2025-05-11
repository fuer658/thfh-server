package com.thfh.service;

import com.thfh.dto.ChatConversationDTO;
import com.thfh.dto.ChatMessageDTO;
import com.thfh.model.ChatMessage;
import com.thfh.model.User;
import com.thfh.repository.ChatMessageRepository;
import com.thfh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 发送消息
     * 
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param content 消息内容
     * @param messageType 消息类型
     * @param mediaUrl 媒体URL（可选）
     * @return 消息DTO
     */
    @Transactional
    public ChatMessageDTO sendMessage(Long senderId, Long receiverId, String content, 
                                     String messageType, String mediaUrl) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("发送者不存在"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("接收者不存在"));

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setMediaUrl(mediaUrl);
        message.setSentTime(LocalDateTime.now());
        message.setRead(false);

        message = chatMessageRepository.save(message);
        
        ChatMessageDTO messageDTO = ChatMessageDTO.fromEntity(message);
        
        // 使用WebSocket向接收者发送消息
        messagingTemplate.convertAndSendToUser(
                receiver.getUsername(),
                "/queue/messages",
                messageDTO
        );

        return messageDTO;
    }

    /**
     * 获取两个用户之间的聊天记录
     * 
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 消息列表
     */
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getMessagesBetweenUsers(Long userId1, Long userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new RuntimeException("用户1不存在"));
        
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new RuntimeException("用户2不存在"));

        List<ChatMessage> messages = chatMessageRepository.findMessagesBetweenUsers(user1, user2);
        
        List<ChatMessageDTO> messageDTOs = new ArrayList<>();
        for (ChatMessage message : messages) {
            messageDTOs.add(ChatMessageDTO.fromEntity(message));
        }

        return messageDTOs;
    }

    /**
     * 获取用户的所有聊天会话
     * 
     * @param userId 用户ID
     * @return 会话列表
     */
    @Transactional(readOnly = true)
    public List<ChatConversationDTO> getUserConversations(Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        List<ChatMessage> recentMessages = chatMessageRepository.findRecentChatsByUser(userId);
        
        Map<Long, ChatConversationDTO> conversationsMap = new HashMap<>();
        
        for (ChatMessage message : recentMessages) {
            // 确定会话对象 (对话的另一方)
            User otherUser = message.getSender().getId().equals(userId) ? 
                              message.getReceiver() : message.getSender();
                              
            // 创建会话DTO或获取现有的
            ChatConversationDTO conversation = conversationsMap.getOrDefault(otherUser.getId(), new ChatConversationDTO());
            
            // 设置用户信息
            conversation.setUserId(otherUser.getId());
            conversation.setUsername(otherUser.getUsername());
            conversation.setAvatar(otherUser.getAvatar());
            
            // 设置最后一条消息
            conversation.setLastMessage(message.getContent());
            conversation.setLastMessageTime(message.getSentTime().format(formatter));
            conversation.setMessageType(message.getMessageType());
            
            // 检查是否有未读消息
            if (message.getReceiver().getId().equals(userId) && !message.isRead()) {
                conversation.setHasUnread(true);
            }
            
            conversationsMap.put(otherUser.getId(), conversation);
        }
        
        // 查询每个会话的未读消息数量
        for (ChatConversationDTO conversation : conversationsMap.values()) {
            // 获取该会话的未读消息数量
            User otherUser = userRepository.findById(conversation.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
            long unreadCount = chatMessageRepository.countUnreadMessagesBetweenUsers(currentUser.getId(), otherUser.getId());
            conversation.setUnreadCount((int) unreadCount);
        }
        
        return new ArrayList<>(conversationsMap.values());
    }

    /**
     * 将消息标记为已读
     * 
     * @param messageId 消息ID
     */
    @Transactional
    public void markMessageAsRead(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        
        message.setRead(true);
        chatMessageRepository.save(message);
    }

    /**
     * 将用户之间的所有消息标记为已读
     * 
     * @param currentUserId 当前用户ID
     * @param otherUserId 另一用户ID
     */
    @Transactional
    public void markAllMessagesAsRead(Long currentUserId, Long otherUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("当前用户不存在"));
        
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new RuntimeException("对方用户不存在"));
        
        List<ChatMessage> messages = chatMessageRepository.findMessagesBetweenUsers(currentUser, otherUser);
        
        for (ChatMessage message : messages) {
            if (message.getReceiver().getId().equals(currentUserId) && !message.isRead()) {
                message.setRead(true);
            }
        }
        
        chatMessageRepository.saveAll(messages);
    }
} 
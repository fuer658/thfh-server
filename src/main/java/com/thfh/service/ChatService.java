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
import com.thfh.exception.BusinessException;
import com.thfh.exception.ErrorCode;
import com.thfh.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
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
                .orElseThrow(() -> {
                    log.warn("发送者不存在, senderId={}", senderId);
                    return new ResourceNotFoundException("发送者不存在");
                });
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> {
                    log.warn("接收者不存在, receiverId={}", receiverId);
                    return new ResourceNotFoundException("接收者不存在");
                });
        if(messageType.equals("TEXT")){
            if (content == null || content.trim().isEmpty()) {
                log.warn("消息内容为空, senderId={}, receiverId={}", senderId, receiverId);
                throw new BusinessException(ErrorCode.PARAMETER_ERROR, "消息内容不能为空");
            }
        }

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
        try {
            messagingTemplate.convertAndSendToUser(
                    receiver.getUsername(),
                    "/queue/messages",
                    messageDTO
            );
        } catch (Exception e) {
            log.error("WebSocket消息推送失败, receiver={}, messageId={}, error={}", receiver.getUsername(), message.getId(), e.getMessage());
        }
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
                .orElseThrow(() -> {
                    log.warn("用户不存在, userId={}", userId1);
                    return new ResourceNotFoundException("用户1不存在");
                });
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> {
                    log.warn("用户不存在, userId={}", userId2);
                    return new ResourceNotFoundException("用户2不存在");
                });
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
                .orElseThrow(() -> {
                    log.warn("用户不存在, userId={}", userId);
                    return new ResourceNotFoundException("用户不存在");
                });
        List<ChatMessage> recentMessages = chatMessageRepository.findRecentChatsByUser(userId);
        Map<Long, ChatConversationDTO> conversationsMap = new HashMap<>();
        for (ChatMessage message : recentMessages) {
            User otherUser = message.getSender().getId().equals(userId) ? 
                              message.getReceiver() : message.getSender();
            ChatConversationDTO conversation = conversationsMap.getOrDefault(otherUser.getId(), new ChatConversationDTO());
            conversation.setUserId(otherUser.getId());
            conversation.setUsername(otherUser.getUsername());
            conversation.setAvatar(otherUser.getAvatar());
            conversation.setLastMessage(message.getContent());
            conversation.setLastMessageTime(message.getSentTime().format(formatter));
            conversation.setMessageType(message.getMessageType());
            if (message.getReceiver().getId().equals(userId) && !message.isRead()) {
                conversation.setHasUnread(true);
            }
            conversationsMap.put(otherUser.getId(), conversation);
        }
        // 查询每个会话的未读消息数量
        for (ChatConversationDTO conversation : conversationsMap.values()) {
            User otherUser = userRepository.findById(conversation.getUserId())
                .orElseThrow(() -> {
                    log.warn("用户不存在, userId={}", conversation.getUserId());
                    return new ResourceNotFoundException("用户不存在");
                });
            long unreadCount = chatMessageRepository.countUnreadMessagesBetweenUsers(currentUser.getId(), otherUser.getId());
            conversation.setUnreadCount((int) unreadCount);
        }
        return new ArrayList<>(conversationsMap.values());
    }

    /**
     * 将消息标记为已读
     * 
     * @param messageId 消息ID
     * @param userId 当前用户ID（用于验证权限）
     */
    @Transactional
    public void markMessageAsRead(Long messageId, Long userId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> {
                    log.warn("消息不存在, messageId={}", messageId);
                    return new ResourceNotFoundException("消息不存在");
                });
        // 验证权限（只有消息的接收者可以标记为已读）
        if (!message.getReceiver().getId().equals(userId)) {
            log.warn("无权限标记消息为已读, messageId={}, userId={}", messageId, userId);
            throw new BusinessException(ErrorCode.FORBIDDEN, "没有权限标记该消息为已读");
        }
        if (message.isRead()) {
            log.info("消息已读, messageId={}", messageId);
            return;
        }
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
                .orElseThrow(() -> {
                    log.warn("当前用户不存在, userId={}", currentUserId);
                    return new ResourceNotFoundException("当前用户不存在");
                });
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> {
                    log.warn("对方用户不存在, userId={}", otherUserId);
                    return new ResourceNotFoundException("对方用户不存在");
                });
        List<ChatMessage> messages = chatMessageRepository.findMessagesBetweenUsers(currentUser, otherUser);
        boolean updated = false;
        for (ChatMessage message : messages) {
            if (message.getReceiver().getId().equals(currentUserId) && !message.isRead()) {
                message.setRead(true);
                updated = true;
            }
        }
        if (updated) {
            chatMessageRepository.saveAll(messages);
        }
    }

    /**
     * 删除单条消息
     *
     * @param messageId 消息ID
     * @param userId 当前用户ID（用于验证权限）
     * @return 删除是否成功
     */
    @Transactional
    public boolean deleteMessage(Long messageId, Long userId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> {
                    log.warn("消息不存在, messageId={}", messageId);
                    return new ResourceNotFoundException("消息不存在");
                });
        // 验证权限（只有消息的发送者或接收者可以删除消息）
        if (!message.getSender().getId().equals(userId) && !message.getReceiver().getId().equals(userId)) {
            log.warn("无权限删除消息, messageId={}, userId={}", messageId, userId);
            throw new BusinessException(ErrorCode.FORBIDDEN, "没有权限删除该消息");
        }
        chatMessageRepository.deleteById(messageId);
        return true;
    }

    /**
     * 删除两个用户之间的所有消息（清空聊天记录）
     *
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 删除的消息数量
     */
    @Transactional
    public int deleteAllMessagesBetweenUsers(Long userId1, Long userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> {
                    log.warn("用户不存在, userId={}", userId1);
                    return new ResourceNotFoundException("用户1不存在");
                });
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> {
                    log.warn("用户不存在, userId={}", userId2);
                    return new ResourceNotFoundException("用户2不存在");
                });
        List<ChatMessage> messages = chatMessageRepository.findMessagesBetweenUsers(user1, user2);
        int count = messages.size();
        if (count > 0) {
            chatMessageRepository.deleteAll(messages);
        }
        return count;
    }
}

package com.thfh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thfh.model.ChatMessage;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class ChatMessageDTO {
    private Long id;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long receiverId;
    private String receiverName;
    private String receiverAvatar;
    private String content;
    private String sentTime;
    
    @JsonProperty("is_read")
    private boolean read;
    
    private String messageType;
    private String mediaUrl;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static ChatMessageDTO fromEntity(ChatMessage message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getUsername());
        dto.setSenderAvatar(message.getSender().getAvatar());
        
        dto.setReceiverId(message.getReceiver().getId());
        dto.setReceiverName(message.getReceiver().getUsername());
        dto.setReceiverAvatar(message.getReceiver().getAvatar());
        
        dto.setContent(message.getContent());
        dto.setRead(message.isRead());
        dto.setMessageType(message.getMessageType());
        dto.setMediaUrl(message.getMediaUrl());
        
        // 格式化时间
        dto.setSentTime(message.getSentTime().format(formatter));
        
        return dto;
    }
} 
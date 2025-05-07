package com.thfh.dto;

import lombok.Data;

/**
 * 聊天消息请求体
 */
@Data
public class ChatMessageRequest {
    private Long receiverId;
    private String content;
    private String messageType = "TEXT"; // 默认为文本消息
    private String mediaUrl;
} 
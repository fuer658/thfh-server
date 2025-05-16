package com.thfh.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 聊天消息请求体
 */
@Data
public class ChatMessageRequest {
    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;
    @NotBlank(message = "消息内容不能为空")
    private String content;
    private String messageType = "TEXT"; // 默认为文本消息
    private String mediaUrl;
} 
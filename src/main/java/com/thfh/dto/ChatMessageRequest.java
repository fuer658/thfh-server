package com.thfh.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

/**
 * 聊天消息请求体
 */
@Data
@Getter
@Setter
public class ChatMessageRequest {
    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;
    private String content;
    private String messageType = "TEXT"; // 默认为文本消息
    private String mediaUrl;
}

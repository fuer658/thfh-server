package com.thfh.dto;

import lombok.Data;

/**
 * 获取聊天记录请求体
 */
@Data
public class GetMessagesRequest {
    private Long otherUserId;
} 
package com.thfh.dto;

import lombok.Data;

/**
 * 标记消息已读请求体
 */
@Data
public class MarkReadRequest {
    private Long otherUserId;
} 
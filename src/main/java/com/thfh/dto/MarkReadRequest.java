package com.thfh.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 标记消息已读请求体
 */
@Data
public class MarkReadRequest {
    @NotNull(message = "对方用户ID不能为空")
    private Long otherUserId;
} 
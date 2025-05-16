package com.thfh.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 获取聊天记录请求体
 */
@Data
public class GetMessagesRequest {
    @NotNull(message = "对方用户ID不能为空")
    private Long otherUserId;
} 
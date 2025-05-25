package com.thfh.dto;

import com.thfh.model.UserOnlineStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户在线状态DTO
 * 用于在前后端之间传输用户在线状态信息
 */
@Data
@Schema(description = "用户在线状态信息 - 包含用户ID、在线状态和最后活跃时间")
public class UserOnlineStatusDTO {
    
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "用户名", example = "zhangsan")
    private String username;
    
    @Schema(description = "在线状态", example = "ONLINE")
    private UserOnlineStatus status;
    
    @Schema(description = "最后活跃时间", example = "2023-01-01T12:00:00")
    private LocalDateTime lastActive;
    
    @Schema(description = "用户头像URL")
    private String avatar;
    
    @Schema(description = "好友备注")
    private String remark;
} 
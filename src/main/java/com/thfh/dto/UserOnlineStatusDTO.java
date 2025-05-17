package com.thfh.dto;

import com.thfh.model.UserOnlineStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户在线状态DTO
 * 用于在前后端之间传输用户在线状态信息
 */
@Data
@ApiModel(value = "用户在线状态信息", description = "包含用户ID、在线状态和最后活跃时间")
public class UserOnlineStatusDTO {
    
    @ApiModelProperty(value = "用户ID", example = "1")
    private Long userId;
    
    @ApiModelProperty(value = "用户名", example = "zhangsan")
    private String username;
    
    @ApiModelProperty(value = "在线状态", example = "ONLINE")
    private UserOnlineStatus status;
    
    @ApiModelProperty(value = "最后活跃时间", example = "2023-01-01T12:00:00")
    private LocalDateTime lastActive;
    
    @ApiModelProperty(value = "用户头像URL")
    private String avatar;
    
    @ApiModelProperty(value = "好友备注")
    private String remark;
} 
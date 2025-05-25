package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户在线状态枚举
 */
@Schema(description = "用户在线状态 - 表示用户当前的在线状态")
public enum UserOnlineStatus {
    @Schema(description = "在线 - 用户当前在线")
    ONLINE("在线"),
    
    @Schema(description = "离线 - 用户当前离线")
    OFFLINE("离线"),
    
    @Schema(description = "忙碌 - 用户当前在线但忙碌")
    BUSY("忙碌"),
    
    @Schema(description = "离开 - 用户暂时离开")
    AWAY("离开"),
    
    @Schema(description = "隐身 - 用户在线但显示为离线")
    INVISIBLE("隐身");
    
    private final String description;
    
    UserOnlineStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 
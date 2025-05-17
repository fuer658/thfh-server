package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户在线状态枚举
 */
@ApiModel(value = "用户在线状态", description = "表示用户当前的在线状态")
public enum UserOnlineStatus {
    @ApiModelProperty(value = "在线", notes = "用户当前在线")
    ONLINE("在线"),
    
    @ApiModelProperty(value = "离线", notes = "用户当前离线")
    OFFLINE("离线"),
    
    @ApiModelProperty(value = "忙碌", notes = "用户当前在线但忙碌")
    BUSY("忙碌"),
    
    @ApiModelProperty(value = "离开", notes = "用户暂时离开")
    AWAY("离开"),
    
    @ApiModelProperty(value = "隐身", notes = "用户在线但显示为离线")
    INVISIBLE("隐身");
    
    private final String description;
    
    UserOnlineStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 
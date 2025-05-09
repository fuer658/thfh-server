package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "积分变动类型", description = "用户积分变动的类型枚举")
public enum PointsType {
    @ApiModelProperty(value = "学习课程", example = "LEARN_COURSE")
    LEARN_COURSE("学习课程"),
    
    @ApiModelProperty(value = "兑换课程", example = "EXCHANGE_COURSE")
    EXCHANGE_COURSE("兑换课程"),
    
    @ApiModelProperty(value = "管理员调整", example = "ADMIN_ADJUST")
    ADMIN_ADJUST("管理员调整");

    private final String description;

    PointsType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
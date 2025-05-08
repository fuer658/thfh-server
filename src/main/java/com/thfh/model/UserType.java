package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户类型枚举
 */
@ApiModel(value = "用户类型", description = "用户类型枚举，区分学员、教员和企业人员")
public enum UserType {
    STUDENT("学员"),
    TEACHER("教员"),
    ENTERPRISE("企业人员");

    @ApiModelProperty(value = "用户类型描述", example = "学员")
    private final String description;

    UserType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
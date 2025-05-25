package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户类型枚举
 */
@Schema(description = "用户类型 - 用户类型枚举，区分学员、教员和企业人员")
public enum UserType {
    STUDENT("学员"),
    TEACHER("教员"),
    ENTERPRISE("企业人员");

    @Schema(description = "用户类型描述", example = "学员")
    private final String description;

    UserType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
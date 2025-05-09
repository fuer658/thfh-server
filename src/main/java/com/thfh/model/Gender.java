package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 性别枚举类
 */
@ApiModel(value = "性别枚举", description = "用户性别类型")
public enum Gender {
    /**
     * 男性
     */
    @ApiModelProperty(value = "男性", example = "MALE")
    MALE("男"),
    
    /**
     * 女性
     */
    @ApiModelProperty(value = "女性", example = "FEMALE")
    FEMALE("女"),
    
    /**
     * 未知
     */
    @ApiModelProperty(value = "未知", example = "UNKNOWN")
    UNKNOWN("未知");

    private final String description;

    Gender(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 安全地解析性别值
     * @param value 要解析的值
     * @return 解析后的性别枚举值，如果无法解析则返回UNKNOWN
     */
    public static Gender fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return UNKNOWN;
        }
        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
} 
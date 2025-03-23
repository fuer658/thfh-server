package com.thfh.model;

/**
 * 性别枚举类
 */
public enum Gender {
    /**
     * 男性
     */
    MALE("男"),
    
    /**
     * 女性
     */
    FEMALE("女"),
    
    /**
     * 未知
     */
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
package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "课程状态枚举类")
public enum CourseStatus {
    @Schema(description = "草稿状态")
    DRAFT("草稿"),
    
    @Schema(description = "已发布状态")
    PUBLISHED("已发布"),
    
    @Schema(description = "已下线状态")
    OFFLINE("已下线");

    private final String description;

    CourseStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
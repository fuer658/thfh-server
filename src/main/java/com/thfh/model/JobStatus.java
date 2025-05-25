package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "职位状态枚举 - 职位的发布状态")
public enum JobStatus {
    @Schema(description = "草稿 - 职位尚未发布，处于编辑状态")
    DRAFT("草稿"),
    
    @Schema(description = "已发布 - 职位已正式发布，可被用户查看")
    PUBLISHED("已发布"),
    
    @Schema(description = "已关闭 - 职位已关闭，不再接受申请")
    CLOSED("已关闭");

    private final String description;

    JobStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
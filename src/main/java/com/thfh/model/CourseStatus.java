package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "课程状态枚举类")
public enum CourseStatus {
    @ApiModelProperty("草稿状态")
    DRAFT("草稿"),
    
    @ApiModelProperty("已发布状态")
    PUBLISHED("已发布"),
    
    @ApiModelProperty("已下线状态")
    OFFLINE("已下线");

    private final String description;

    CourseStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
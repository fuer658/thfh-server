package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "职位状态枚举", description = "职位的发布状态")
public enum JobStatus {
    @ApiModelProperty(value = "草稿", notes = "职位尚未发布，处于编辑状态")
    DRAFT("草稿"),
    
    @ApiModelProperty(value = "已发布", notes = "职位已正式发布，可被用户查看")
    PUBLISHED("已发布"),
    
    @ApiModelProperty(value = "已关闭", notes = "职位已关闭，不再接受申请")
    CLOSED("已关闭");

    private final String description;

    JobStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
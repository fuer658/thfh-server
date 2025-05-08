package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 文件类型枚举
 * 用于区分不同类型的文件
 */
@ApiModel(value = "文件类型枚举", description = "用于区分不同类型的文件")
public enum FileType {
    @ApiModelProperty(value = "图片", example = "IMAGE")
    IMAGE("图片"),
    
    @ApiModelProperty(value = "视频", example = "VIDEO")
    VIDEO("视频"),
    
    @ApiModelProperty(value = "音频", example = "AUDIO")
    AUDIO("音频"),
    
    @ApiModelProperty(value = "文档", example = "DOCUMENT")
    DOCUMENT("文档"),
    
    @ApiModelProperty(value = "归档文件", example = "ARCHIVE")
    ARCHIVE("归档文件"),
    
    @ApiModelProperty(value = "其他", example = "OTHER")
    OTHER("其他");

    private final String description;

    FileType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
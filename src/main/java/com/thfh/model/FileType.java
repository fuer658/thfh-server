package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 文件类型枚举
 * 用于区分不同类型的文件
 */
@Schema(description = "文件类型枚举 - 用于区分不同类型的文件")
public enum FileType {
    @Schema(description = "图片", example = "IMAGE")
    IMAGE("图片"),
    
    @Schema(description = "视频", example = "VIDEO")
    VIDEO("视频"),
    
    @Schema(description = "音频", example = "AUDIO")
    AUDIO("音频"),
    
    @Schema(description = "文档", example = "DOCUMENT")
    DOCUMENT("文档"),
    
    @Schema(description = "归档文件", example = "ARCHIVE")
    ARCHIVE("归档文件"),
    
    @Schema(description = "其他", example = "OTHER")
    OTHER("其他");

    private final String description;

    FileType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
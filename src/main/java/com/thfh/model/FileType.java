package com.thfh.model;

/**
 * 文件类型枚举
 * 用于区分不同类型的文件
 */
public enum FileType {
    IMAGE("图片"),
    VIDEO("视频"),
    AUDIO("音频"),
    DOCUMENT("文档"),
    ARCHIVE("归档文件"),
    OTHER("其他");

    private final String description;

    FileType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
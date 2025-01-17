package com.thfh.model;

public enum JobStatus {
    DRAFT("草稿"),
    PUBLISHED("已发布"),
    CLOSED("已关闭");

    private final String description;

    JobStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
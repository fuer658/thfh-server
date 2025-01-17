package com.thfh.model;

public enum CourseStatus {
    DRAFT("草稿"),
    PUBLISHED("已发布"),
    OFFLINE("已下线");

    private final String description;

    CourseStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
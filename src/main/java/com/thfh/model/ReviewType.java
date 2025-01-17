package com.thfh.model;

public enum ReviewType {
    COURSE("课程评价"),
    WORK("作品评价");

    private final String description;

    ReviewType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
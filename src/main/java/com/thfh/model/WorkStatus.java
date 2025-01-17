package com.thfh.model;

public enum WorkStatus {
    DRAFT("草稿"),
    PENDING("待审核"),
    ON_SALE("在售"),
    SOLD_OUT("已售罄"),
    OFFLINE("已下架");

    private final String description;

    WorkStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
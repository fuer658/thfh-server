package com.thfh.model;

public enum PointsType {
    LEARN_COURSE("学习课程"),
    EXCHANGE_COURSE("兑换课程"),
    ADMIN_ADJUST("管理员调整");

    private final String description;

    PointsType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
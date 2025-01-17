package com.thfh.model;

public enum UserType {
    STUDENT("学员"),
    TEACHER("教员");

    private final String description;

    UserType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 
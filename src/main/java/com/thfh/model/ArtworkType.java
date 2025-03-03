package com.thfh.model;

public enum ArtworkType {
    PERSONAL("个人作品"),
    COMMERCIAL("商业作品"),
    ENTERPRISE("企业作品");

    private final String description;

    ArtworkType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
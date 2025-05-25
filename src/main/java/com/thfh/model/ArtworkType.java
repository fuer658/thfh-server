package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "作品类型 - 作品的分类类型")
public enum ArtworkType {
    @Schema(description = "个人作品")
    PERSONAL("个人作品"),
    @Schema(description = "商业作品")
    COMMERCIAL("商业作品"),
    @Schema(description = "企业作品")
    ENTERPRISE("企业作品");

    @Schema(description = "类型描述")
    private final String description;

    ArtworkType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
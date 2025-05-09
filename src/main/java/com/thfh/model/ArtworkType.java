package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "作品类型", description = "作品的分类类型")
public enum ArtworkType {
    @ApiModelProperty("个人作品")
    PERSONAL("个人作品"),
    @ApiModelProperty("商业作品")
    COMMERCIAL("商业作品"),
    @ApiModelProperty("企业作品")
    ENTERPRISE("企业作品");

    @ApiModelProperty("类型描述")
    private final String description;

    ArtworkType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
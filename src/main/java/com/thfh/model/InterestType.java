package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 兴趣类型枚举
 * 定义用户可以选择的兴趣爱好类型
 */
@ApiModel(value = "兴趣类型枚举", description = "用户可以选择的兴趣爱好类型")
public enum InterestType {
    @ApiModelProperty(value = "纹样设计", example = "PATTERN_DESIGN")
    PATTERN_DESIGN("纹样设计"),
    
    @ApiModelProperty(value = "国风饰品", example = "TRADITIONAL_ACCESSORIES")
    TRADITIONAL_ACCESSORIES("国风饰品"),
    
    @ApiModelProperty(value = "汉服形制", example = "HANFU_DESIGN")
    HANFU_DESIGN("汉服形制"),
    
    @ApiModelProperty(value = "广西壮锦", example = "GUANGXI_BROCADE")
    GUANGXI_BROCADE("广西壮锦"),
    
    @ApiModelProperty(value = "非遗剪纸", example = "INTANGIBLE_PAPER_CUTTING")
    INTANGIBLE_PAPER_CUTTING("非遗剪纸"),
    
    @ApiModelProperty(value = "国潮插画", example = "NATIONAL_ILLUSTRATION")
    NATIONAL_ILLUSTRATION("国潮插画"),
    
    @ApiModelProperty(value = "刺绣工艺", example = "EMBROIDERY_CRAFT")
    EMBROIDERY_CRAFT("刺绣工艺"),
    
    @ApiModelProperty(value = "陶艺制作", example = "POTTERY_MAKING")
    POTTERY_MAKING("陶艺制作");

    private final String displayName;

    InterestType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 
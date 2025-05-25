package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 兴趣类型枚举
 * 定义用户可以选择的兴趣爱好类型
 */
@Schema(description = "兴趣类型枚举 - 用户可以选择的兴趣爱好类型")
public enum InterestType {
    @Schema(description = "纹样设计", example = "PATTERN_DESIGN")
    PATTERN_DESIGN("纹样设计"),
    
    @Schema(description = "国风饰品", example = "TRADITIONAL_ACCESSORIES")
    TRADITIONAL_ACCESSORIES("国风饰品"),
    
    @Schema(description = "汉服形制", example = "HANFU_DESIGN")
    HANFU_DESIGN("汉服形制"),
    
    @Schema(description = "广西壮锦", example = "GUANGXI_BROCADE")
    GUANGXI_BROCADE("广西壮锦"),
    
    @Schema(description = "非遗剪纸", example = "INTANGIBLE_PAPER_CUTTING")
    INTANGIBLE_PAPER_CUTTING("非遗剪纸"),
    
    @Schema(description = "国潮插画", example = "NATIONAL_ILLUSTRATION")
    NATIONAL_ILLUSTRATION("国潮插画"),
    
    @Schema(description = "刺绣工艺", example = "EMBROIDERY_CRAFT")
    EMBROIDERY_CRAFT("刺绣工艺"),
    
    @Schema(description = "陶艺制作", example = "POTTERY_MAKING")
    POTTERY_MAKING("陶艺制作");

    private final String displayName;

    InterestType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 
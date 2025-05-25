package com.thfh.dto;

import com.thfh.model.InterestType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户兴趣数据传输对象
 */
@Data
@Schema(description = "用户兴趣DTO - 用于用户兴趣相关的请求和响应")
public class UserInterestDTO {
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "兴趣类型列表 - 可选值包括：PATTERN_DESIGN（纹样设计）、TRADITIONAL_ACCESSORIES（国风饰品）等")
    private List<InterestType> interests;
} 
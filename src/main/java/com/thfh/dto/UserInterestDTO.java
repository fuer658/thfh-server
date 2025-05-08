package com.thfh.dto;

import com.thfh.model.InterestType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 用户兴趣数据传输对象
 */
@Data
@ApiModel(value = "用户兴趣DTO", description = "用于用户兴趣相关的请求和响应")
public class UserInterestDTO {
    @ApiModelProperty(value = "用户ID", example = "1")
    private Long userId;
    
    @ApiModelProperty(value = "兴趣类型列表", notes = "可选值包括：PATTERN_DESIGN（纹样设计）、TRADITIONAL_ACCESSORIES（国风饰品）等")
    private List<InterestType> interests;
} 
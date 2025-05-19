package com.thfh.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 刷新令牌请求DTO
 */
@Data
@ApiModel(description = "刷新令牌请求")
public class RefreshTokenRequest {
    
    @NotBlank(message = "刷新令牌不能为空")
    @ApiModelProperty(value = "刷新令牌", required = true, example = "eyJhbGciOiJIUzUxMiJ9...")
    private String refreshToken;
    
    @ApiModelProperty(value = "是否同时刷新刷新令牌", required = false, example = "false")
    private Boolean refreshBoth = false;
} 
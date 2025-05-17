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
    
    @NotBlank(message = "令牌不能为空")
    @ApiModelProperty(value = "当前JWT令牌", required = true, example = "eyJhbGciOiJIUzUxMiJ9...")
    private String token;
} 
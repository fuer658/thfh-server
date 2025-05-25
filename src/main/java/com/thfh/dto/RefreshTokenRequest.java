package com.thfh.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 刷新令牌请求DTO
 */
@Data
@Schema(description = "刷新令牌请求")
public class RefreshTokenRequest {
    
    @NotBlank(message = "刷新令牌不能为空")
    @Schema(description = "刷新令牌", required = true, example = "eyJhbGciOiJIUzUxMiJ9...")
    private String refreshToken;
    
    @Schema(description = "是否同时刷新刷新令牌", required = false, example = "false")
    private Boolean refreshBoth = false;
} 
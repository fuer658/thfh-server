package com.thfh.dto;

import javax.validation.constraints.NotBlank;

/**
 * 用于JWT校验请求的DTO
 */
public class JwtVerifyRequest {
    @NotBlank(message = "token不能为空")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
} 
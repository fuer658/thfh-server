package com.thfh.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录数据传输对象
 * 用于接收前端登录请求的用户名和密码
 */
@Data
public class LoginDTO {
    /**
     * 用户名
     * 用于登录验证，不能为空
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     * 用于登录验证，不能为空
     */
    @NotBlank(message = "密码不能为空")
    private String password;
} 
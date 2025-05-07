package com.thfh.dto;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * 登录数据传输对象
 * 用于接收前端登录请求的用户名和密码
 */
@Data
@ApiModel(value = "登录参数", description = "用于接收前端登录请求的用户名和密码")
public class LoginDTO {
    /**
     * 用户名
     * 用于登录验证，不能为空
     */
    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名", required = true, example = "user123")
    private String username;

    /**
     * 密码
     * 用于登录验证，不能为空
     */
    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true, example = "password123")
    private String password;
} 
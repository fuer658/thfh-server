package com.thfh.dto;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 修改密码数据传输对象
 * 用于接收前端修改密码请求的旧密码和新密码
 */
@Data
@ApiModel(value = "修改密码参数", description = "用于接收前端修改密码请求的旧密码和新密码")
public class ChangePasswordDTO {
    /**
     * 旧密码
     * 用于验证用户身份，不能为空
     */
    @NotBlank(message = "旧密码不能为空")
    @ApiModelProperty(value = "旧密码", required = true, example = "oldPassword123")
    private String oldPassword;

    /**
     * 新密码
     * 用户希望更新的新密码，不能为空且长度必须在6到20之间
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度必须在6到20之间")
    @ApiModelProperty(value = "新密码", required = true, example = "newPassword123")
    private String newPassword;
} 
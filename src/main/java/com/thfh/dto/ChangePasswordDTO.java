package com.thfh.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 修改密码数据传输对象
 * 用于接收前端修改密码请求的旧密码和新密码
 */
@Data
@Schema(description = "修改密码参数 - 用于接收前端修改密码请求的旧密码和新密码")
public class ChangePasswordDTO {
    /**
     * 旧密码
     * 用于验证用户身份，不能为空
     */
    @NotBlank(message = "旧密码不能为空")
    @Schema(description = "旧密码", required = true, example = "oldPassword123")
    private String oldPassword;

    /**
     * 新密码
     * 用户希望更新的新密码，不能为空且长度必须在6到20之间
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度必须在6到20之间")
    @Schema(description = "新密码", required = true, example = "newPassword123")
    private String newPassword;
} 
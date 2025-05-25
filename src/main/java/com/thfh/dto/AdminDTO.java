package com.thfh.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 管理员数据传输对象
 * 用于在不同层之间传输管理员信息
 */
@Data
@Schema(description = "管理员信息 - 包含管理员的基本信息")
public class AdminDTO {
    /**
     * 管理员ID，唯一标识
     */
    @Schema(description = "管理员ID", description = "唯一标识", example = "1")
    private Long id;
    
    /**
     * 管理员用户名，用于登录
     */
    @Schema(description = "用户名", required = true, description = "用于登录系统", example = "admin123")
    private String username;
    
    /**
     * 管理员密码，用于登录验证
     */
    @Schema(description = "密码", description = "创建管理员时必填，更新时可不填", example = "password123")
    private String password;
    
    /**
     * 管理员真实姓名
     */
    @Schema(description = "真实姓名", example = "张三")
    private String realName;
    
    /**
     * 管理员手机号码
     */
    @Schema(description = "手机号码", example = "13800138000")
    private String phone;
    
    /**
     * 管理员电子邮箱
     */
    @Schema(description = "电子邮箱", example = "admin@example.com")
    private String email;
    
    /**
     * 管理员账号是否启用
     */
    @Schema(description = "是否启用", description = "true-启用，false-禁用", example = "true")
    private Boolean enabled;
}
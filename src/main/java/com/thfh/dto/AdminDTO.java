package com.thfh.dto;

import lombok.Data;

/**
 * 管理员数据传输对象
 * 用于在不同层之间传输管理员信息
 */
@Data
public class AdminDTO {
    /**
     * 管理员ID，唯一标识
     */
    private Long id;
    
    /**
     * 管理员用户名，用于登录
     */
    private String username;
    
    /**
     * 管理员密码，用于登录验证
     */
    private String password;
    
    /**
     * 管理员真实姓名
     */
    private String realName;
    
    /**
     * 管理员手机号码
     */
    private String phone;
    
    /**
     * 管理员电子邮箱
     */
    private String email;
    
    /**
     * 管理员账号是否启用
     */
    private Boolean enabled;
}
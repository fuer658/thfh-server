package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "admin")
@Schema(description = "管理员 - 系统管理员信息")
public class Admin {
    @Schema(description = "管理员ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "用户名", required = true, example = "admin")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Schema(description = "密码", required = true, example = "******")
    @Column(nullable = false)
    private String password;

    @Schema(description = "真实姓名", example = "张三")
    @Column(length = 100)
    private String realName;

    @Schema(description = "手机号码", example = "13800138000")
    @Column(length = 50)
    private String phone;

    @Schema(description = "电子邮箱", example = "admin@example.com")
    @Column(length = 100)
    private String email;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "是否启用", example = "true")
    @Column(nullable = false)
    private Boolean enabled = true;

    @Schema(description = "创建时间")
    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @Schema(description = "更新时间")
    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now();
} 
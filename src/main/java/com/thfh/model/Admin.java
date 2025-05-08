package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "admin")
@ApiModel(value = "管理员", description = "系统管理员信息")
public class Admin {
    @ApiModelProperty(value = "管理员ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "用户名", required = true, example = "admin")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @ApiModelProperty(value = "密码", required = true, example = "******")
    @Column(nullable = false)
    private String password;

    @ApiModelProperty(value = "真实姓名", example = "张三")
    @Column(length = 100)
    private String realName;

    @ApiModelProperty(value = "手机号码", example = "13800138000")
    @Column(length = 50)
    private String phone;

    @ApiModelProperty(value = "电子邮箱", example = "admin@example.com")
    @Column(length = 100)
    private String email;

    @ApiModelProperty(value = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @ApiModelProperty(value = "是否启用", example = "true")
    @Column(nullable = false)
    private Boolean enabled = true;

    @ApiModelProperty(value = "创建时间")
    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @ApiModelProperty(value = "更新时间")
    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now();
} 
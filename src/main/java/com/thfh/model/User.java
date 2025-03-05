package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String realName;

    @Column(length = 100)
    private String Locate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType; // STUDENT(学员) 或 TEACHER(教员)

    @Column(length = 50)
    private String phone;

    @Column(length = 100)
    private String email;

    private String avatar;

    @Column(length = 500)
    private String introduction;

    // 教员特有字段
    private String qualification; // 资质证书
    private String speciality; // 专长领域

    // 学员特有字段
    private String disability; // 残疾类型
    private Integer points = 0; // 积分

    @Column(nullable = false)
    private Boolean enabled = true;

    private LocalDateTime lastLoginTime;

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now();

    @Column
    private LocalDate birthday;
}
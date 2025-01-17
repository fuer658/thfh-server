package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 公司名称

    private String logo; // 公司logo

    @Column(length = 1000)
    private String description; // 公司简介

    private String industry; // 所属行业

    private String scale; // 公司规模

    private String website; // 公司网站

    private String address; // 公司地址

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now();
} 
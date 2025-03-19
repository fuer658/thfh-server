package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "job")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 职位标题

    @Column(length = 1000)
    private String description; // 职位描述

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // 所属公司

    // 添加与职位分类的关联
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private JobCategory category; // 所属分类

    @Column(nullable = false)
    private String location; // 工作地点

    @Column(nullable = false)
    private BigDecimal salaryMin; // 最低薪资

    @Column(nullable = false)
    private BigDecimal salaryMax; // 最高薪资

    @Column(nullable = false)
    private String requirements; // 任职要求

    @Column(nullable = false)
    private String benefits; // 福利待遇

    @Column(nullable = false)
    private String disabilitySupport; // 残疾人支持措施

    @Column(nullable = false)
    private String contactPerson; // 联系人

    private String contactPhone; // 联系电话

    private String contactEmail; // 联系邮箱

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobStatus status; // 职位状态

    private Integer viewCount = 0; // 浏览次数

    private Integer applyCount = 0; // 申请次数

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now();
    
    // 添加与JobApplication的一对多关联
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> applications = new ArrayList<>();
    
    /**
     * 更新实体的updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 初始化实体的createTime和updateTime
     */
    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
} 
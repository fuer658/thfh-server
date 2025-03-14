package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 职位申请实体类
 * 记录用户对职位的申请信息
 */
@Data
@Entity
@Table(name = "job_application")
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job; // 申请的职位

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 申请人

    @Column(nullable = false)
    private String resumeUrl; // 简历链接

    @Column(length = 1000)
    private String coverLetter; // 求职信/自我介绍

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobApplicationStatus status = JobApplicationStatus.PENDING; // 申请状态，默认为待处理

    private String rejectionReason; // 拒绝原因（如果被拒绝）

    @Column(length = 1000)
    private String notes; // 面试/审核备注

    private LocalDateTime interviewTime; // 面试时间

    private String interviewLocation; // 面试地点

    private Boolean isRead = false; // 是否已读（企业查看）

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now(); // 创建时间

    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now(); // 更新时间
    
    /**
     * 更新实体的updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 
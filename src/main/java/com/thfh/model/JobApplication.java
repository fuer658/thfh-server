package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 职位申请实体类
 * 记录用户对职位的申请信息
 */
@Data
@Entity
@Table(name = "job_application")
@Schema(description = "职位申请 - 记录用户对职位的申请信息")
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "申请ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    @Schema(description = "申请的职位", required = true)
    private Job job; // 申请的职位

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "申请人", required = true)
    private User user; // 申请人

    @Column(nullable = false)
    @Schema(description = "简历链接", required = true, example = "https://example.com/resume.pdf")
    private String resumeUrl; // 简历链接

    @Column(length = 1000)
    @Schema(description = "求职信/自我介绍", example = "我是一名有5年经验的前端开发工程师...")
    private String coverLetter; // 求职信/自我介绍

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "申请状态 - 可选值: PENDING, REVIEWING, INTERVIEW, ACCEPTED, REJECTED, WITHDRAWN", required = true, example = "PENDING")
    private JobApplicationStatus status = JobApplicationStatus.PENDING; // 申请状态，默认为待处理

    @Schema(description = "拒绝原因 - 状态为REJECTED时的拒绝理由", example = "经验不足")
    private String rejectionReason; // 拒绝原因（如果被拒绝）

    @Column(length = 1000)
    @Schema(description = "面试/审核备注", example = "候选人技术背景匹配，安排下周面试")
    private String notes; // 面试/审核备注

    @Schema(description = "面试时间", example = "2023-06-01T10:00:00")
    private LocalDateTime interviewTime; // 面试时间

    @Schema(description = "面试地点", example = "公司总部3楼会议室")
    private String interviewLocation; // 面试地点

    @Schema(description = "是否已读 - 企业是否已查看该申请", example = "false")
    private Boolean isRead = false; // 是否已读（企业查看）

    @Column(nullable = false)
    @Schema(description = "创建时间", example = "2023-05-20T14:30:00")
    private LocalDateTime createTime = LocalDateTime.now(); // 创建时间

    @Column(nullable = false)
    @Schema(description = "更新时间", example = "2023-05-20T14:30:00")
    private LocalDateTime updateTime = LocalDateTime.now(); // 更新时间
    
    /**
     * 更新实体的updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 
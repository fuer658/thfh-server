package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "职位申请", description = "记录用户对职位的申请信息")
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "申请ID", example = "1", position = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    @ApiModelProperty(value = "申请的职位", required = true, position = 2)
    private Job job; // 申请的职位

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ApiModelProperty(value = "申请人", required = true, position = 3)
    private User user; // 申请人

    @Column(nullable = false)
    @ApiModelProperty(value = "简历链接", required = true, example = "https://example.com/resume.pdf", position = 4)
    private String resumeUrl; // 简历链接

    @Column(length = 1000)
    @ApiModelProperty(value = "求职信/自我介绍", example = "我是一名有5年经验的前端开发工程师...", position = 5)
    private String coverLetter; // 求职信/自我介绍

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "申请状态", required = true, example = "PENDING", notes = "可选值: PENDING, REVIEWING, INTERVIEW, ACCEPTED, REJECTED, WITHDRAWN", position = 6)
    private JobApplicationStatus status = JobApplicationStatus.PENDING; // 申请状态，默认为待处理

    @ApiModelProperty(value = "拒绝原因", example = "经验不足", notes = "状态为REJECTED时的拒绝理由", position = 7)
    private String rejectionReason; // 拒绝原因（如果被拒绝）

    @Column(length = 1000)
    @ApiModelProperty(value = "面试/审核备注", example = "候选人技术背景匹配，安排下周面试", position = 8)
    private String notes; // 面试/审核备注

    @ApiModelProperty(value = "面试时间", example = "2023-06-01T10:00:00", position = 9)
    private LocalDateTime interviewTime; // 面试时间

    @ApiModelProperty(value = "面试地点", example = "公司总部3楼会议室", position = 10)
    private String interviewLocation; // 面试地点

    @ApiModelProperty(value = "是否已读", example = "false", notes = "企业是否已查看该申请", position = 11)
    private Boolean isRead = false; // 是否已读（企业查看）

    @Column(nullable = false)
    @ApiModelProperty(value = "创建时间", example = "2023-05-20T14:30:00", position = 12)
    private LocalDateTime createTime = LocalDateTime.now(); // 创建时间

    @Column(nullable = false)
    @ApiModelProperty(value = "更新时间", example = "2023-05-20T14:30:00", position = 13)
    private LocalDateTime updateTime = LocalDateTime.now(); // 更新时间
    
    /**
     * 更新实体的updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 
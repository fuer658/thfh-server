package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "job")
@Schema(description = "职位信息 - 职位的详细信息")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "职位ID", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "职位标题", required = true, example = "前端开发工程师")
    private String title; // 职位标题

    @Column(length = 1000)
    @Schema(description = "职位描述", example = "负责公司官网及管理系统的前端开发维护工作")
    private String description; // 职位描述

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @Schema(description = "所属公司", required = true)
    private Company company; // 所属公司

    // 添加与职位分类的关联
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @Schema(description = "所属分类")
    private JobCategory category; // 所属分类

    @Column(nullable = false)
    @Schema(description = "工作地点", required = true, example = "北京市海淀区")
    private String location; // 工作地点

    @Column(nullable = false)
    @Schema(description = "最低薪资", required = true, example = "8000")
    private BigDecimal salaryMin; // 最低薪资

    @Column(nullable = false)
    @Schema(description = "最高薪资", required = true, example = "15000")
    private BigDecimal salaryMax; // 最高薪资

    @Column(nullable = false)
    @Schema(description = "任职要求", required = true, example = "熟悉Vue.js前端框架，有2年以上相关工作经验")
    private String requirements; // 任职要求

    @Column(nullable = false)
    @Schema(description = "福利待遇", required = true, example = "五险一金，年终奖，带薪年假")
    private String benefits; // 福利待遇

    @Column(nullable = false)
    @Schema(description = "残疾人支持措施", required = true, example = "无障碍工作环境，灵活工作时间")
    private String disabilitySupport; // 残疾人支持措施

    @Column(nullable = false)
    @Schema(description = "联系人", required = true, example = "张先生")
    private String contactPerson; // 联系人

    @Schema(description = "联系电话", example = "13800138000")
    private String contactPhone; // 联系电话

    @Schema(description = "联系邮箱", example = "hr@example.com")
    private String contactEmail; // 联系邮箱
    
    @Schema(description = "上班时间", example = "9:00-18:00")
    private String workStartTime; // 上班时间 (从Company移动过来)
    
    @Schema(description = "下班时间", example = "9:00-18:00")
    private String workEndTime; // 下班时间 (从Company移动过来)

    @Schema(description = "公司待遇", example = "五险一金，节日福利")
    private String companyTreatment; // 公司待遇 (从Company移动过来)

    @Schema(description = "员工福利", example = "免费工作餐，定期团建")
    private String employeeBenefits; // 员工福利 (从Company移动过来)

    @Schema(description = "晋升待遇", example = "完善的晋升机制，定期绩效考核")
    private String promotionProspects; // 晋升待遇 (从Company移动过来)

    @Schema(description = "岗位要求", example = "本科及以上学历，计算机相关专业")
    private String jobRequirements; // 岗位要求 (从Company移动过来)

    @Column(length = 500)
    @Schema(description = "招聘岗位 - 多个岗位以逗号分隔", example = "前端开发,后端开发")
    private String positions; // 招聘岗位，多个岗位以逗号分隔 (从Company移动过来)
    
    @Column(length = 500)
    @Schema(description = "职位标签 - 多个标签以逗号分隔", example = "远程办公,弹性工作,五险一金")
    private String tags; // 职位标签，多个标签以逗号分隔，用于前端展示

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "职位状态", required = true, example = "PUBLISHED")
    private JobStatus status = JobStatus.DRAFT; // 职位状态：草稿、已发布、已关闭

    @Schema(description = "浏览次数", example = "100")
    private Integer viewCount = 0; // 浏览次数

    @Schema(description = "申请次数", example = "10")
    private Integer applyCount = 0; // 申请次数

    @Column(nullable = false)
    @Schema(description = "是否启用", example = "true")
    private Boolean enabled = true;

    @Column(nullable = false)
    @Schema(description = "创建时间", example = "2023-05-20T14:30:00")
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    @Schema(description = "更新时间", example = "2023-05-20T14:30:00")
    private LocalDateTime updateTime = LocalDateTime.now();
    
    // 添加与JobApplication的一对多关联
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "职位申请列表 - 该职位收到的所有申请")
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
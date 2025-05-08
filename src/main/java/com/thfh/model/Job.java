package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "job")
@ApiModel(value = "职位信息", description = "职位的详细信息")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "职位ID", example = "1", position = 1)
    private Long id;

    @Column(nullable = false)
    @ApiModelProperty(value = "职位标题", required = true, example = "前端开发工程师", position = 2)
    private String title; // 职位标题

    @Column(length = 1000)
    @ApiModelProperty(value = "职位描述", example = "负责公司官网及管理系统的前端开发维护工作", position = 3)
    private String description; // 职位描述

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @ApiModelProperty(value = "所属公司", required = true, position = 4)
    private Company company; // 所属公司

    // 添加与职位分类的关联
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ApiModelProperty(value = "所属分类", position = 5)
    private JobCategory category; // 所属分类

    @Column(nullable = false)
    @ApiModelProperty(value = "工作地点", required = true, example = "北京市海淀区", position = 6)
    private String location; // 工作地点

    @Column(nullable = false)
    @ApiModelProperty(value = "最低薪资", required = true, example = "8000", position = 7)
    private BigDecimal salaryMin; // 最低薪资

    @Column(nullable = false)
    @ApiModelProperty(value = "最高薪资", required = true, example = "15000", position = 8)
    private BigDecimal salaryMax; // 最高薪资

    @Column(nullable = false)
    @ApiModelProperty(value = "任职要求", required = true, example = "熟悉Vue.js前端框架，有2年以上相关工作经验", position = 9)
    private String requirements; // 任职要求

    @Column(nullable = false)
    @ApiModelProperty(value = "福利待遇", required = true, example = "五险一金，年终奖，带薪年假", position = 10)
    private String benefits; // 福利待遇

    @Column(nullable = false)
    @ApiModelProperty(value = "残疾人支持措施", required = true, example = "无障碍工作环境，灵活工作时间", position = 11)
    private String disabilitySupport; // 残疾人支持措施

    @Column(nullable = false)
    @ApiModelProperty(value = "联系人", required = true, example = "张先生", position = 12)
    private String contactPerson; // 联系人

    @ApiModelProperty(value = "联系电话", example = "13800138000", position = 13)
    private String contactPhone; // 联系电话

    @ApiModelProperty(value = "联系邮箱", example = "hr@example.com", position = 14)
    private String contactEmail; // 联系邮箱
    
    @ApiModelProperty(value = "上班时间", example = "9:00-18:00", position = 15)
    private String workStartTime; // 上班时间 (从Company移动过来)
    
    @ApiModelProperty(value = "下班时间", example = "9:00-18:00", position = 16)
    private String workEndTime; // 下班时间 (从Company移动过来)

    @ApiModelProperty(value = "公司待遇", example = "五险一金，节日福利", position = 17)
    private String companyTreatment; // 公司待遇 (从Company移动过来)

    @ApiModelProperty(value = "员工福利", example = "免费工作餐，定期团建", position = 18)
    private String employeeBenefits; // 员工福利 (从Company移动过来)

    @ApiModelProperty(value = "晋升待遇", example = "完善的晋升机制，定期绩效考核", position = 19)
    private String promotionProspects; // 晋升待遇 (从Company移动过来)

    @ApiModelProperty(value = "岗位要求", example = "本科及以上学历，计算机相关专业", position = 20)
    private String jobRequirements; // 岗位要求 (从Company移动过来)

    @Column(length = 500)
    @ApiModelProperty(value = "招聘岗位", example = "前端开发,后端开发", notes = "多个岗位以逗号分隔", position = 21)
    private String positions; // 招聘岗位，多个岗位以逗号分隔 (从Company移动过来)
    
    @Column(length = 500)
    @ApiModelProperty(value = "职位标签", example = "远程办公,弹性工作,五险一金", notes = "多个标签以逗号分隔", position = 22)
    private String tags; // 职位标签，多个标签以逗号分隔，用于前端展示

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "职位状态", required = true, example = "PUBLISHED", position = 23)
    private JobStatus status = JobStatus.DRAFT; // 职位状态：草稿、已发布、已关闭

    @ApiModelProperty(value = "浏览次数", example = "100", position = 24)
    private Integer viewCount = 0; // 浏览次数

    @ApiModelProperty(value = "申请次数", example = "10", position = 25)
    private Integer applyCount = 0; // 申请次数

    @Column(nullable = false)
    @ApiModelProperty(value = "是否启用", example = "true", position = 26)
    private Boolean enabled = true;

    @Column(nullable = false)
    @ApiModelProperty(value = "创建时间", example = "2023-05-20T14:30:00", position = 27)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    @ApiModelProperty(value = "更新时间", example = "2023-05-20T14:30:00", position = 28)
    private LocalDateTime updateTime = LocalDateTime.now();
    
    // 添加与JobApplication的一对多关联
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @ApiModelProperty(value = "职位申请列表", notes = "该职位收到的所有申请", position = 29)
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
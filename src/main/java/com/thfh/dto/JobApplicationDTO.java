package com.thfh.dto;

import com.thfh.model.JobApplicationStatus;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * 职位申请数据传输对象
 * 用于前后端数据交换
 */
@Data
@Schema(description = "职位申请信息 - 包含职位申请的详细信息")
public class JobApplicationDTO {
    /**
     * 申请ID
     */
    @Schema(description = "申请ID", description = "唯一标识", example = "1")
    private Long id;
    
    /**
     * 职位ID
     */
    @Schema(description = "职位ID", example = "100")
    private Long jobId;
    
    /**
     * 职位标题
     */
    @Schema(description = "职位标题", example = "软件工程师")
    private String jobTitle;
    
    /**
     * 申请人ID
     */
    @Schema(description = "申请人ID", example = "200")
    private Long userId;
    
    /**
     * 申请人用户名
     */
    @Schema(description = "申请人用户名", example = "user123")
    private String username;
    
    /**
     * 申请人真实姓名
     */
    @Schema(description = "申请人真实姓名", example = "张三")
    private String realName;
    
    /**
     * 申请人头像
     */
    @Schema(description = "申请人头像", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    /**
     * 简历链接
     */
    @Schema(description = "简历链接", example = "https://example.com/resume.pdf")
    private String resumeUrl;
    
    /**
     * 求职信/自我介绍
     */
    @Schema(description = "求职信", description = "自我介绍", example = "我是一名有5年经验的开发人员...")
    private String coverLetter;
    
    /**
     * 申请状态
     */
    @Schema(description = "申请状态", example = "PENDING")
    private JobApplicationStatus status;
    
    /**
     * 拒绝原因（如果被拒绝）
     */
    @Schema(description = "拒绝原因", description = "如果被拒绝", example = "您的技能与岗位要求不匹配")
    private String rejectionReason;
    
    /**
     * 面试/审核备注
     */
    @Schema(description = "面试备注", description = "面试/审核备注", example = "候选人沟通能力良好")
    private String notes;
    
    /**
     * 面试时间
     */
    @Schema(description = "面试时间", example = "2023-01-15 14:00:00")
    private LocalDateTime interviewTime;
    
    /**
     * 面试地点
     */
    @Schema(description = "面试地点", example = "北京市海淀区XX大厦5层")
    private String interviewLocation;
    
    /**
     * 是否已读
     */
    @Schema(description = "是否已读", example = "true")
    private Boolean isRead;
    
    /**
     * 申请创建时间
     */
    @Schema(description = "创建时间", example = "2023-01-01 10:00:00")
    private LocalDateTime createTime;
    
    /**
     * 申请更新时间
     */
    @Schema(description = "更新时间", example = "2023-01-02 15:30:00")
    private LocalDateTime updateTime;
} 
package com.thfh.dto;

import com.thfh.model.JobApplicationStatus;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;

/**
 * 职位申请数据传输对象
 * 用于前后端数据交换
 */
@Data
@ApiModel(value = "职位申请信息", description = "包含职位申请的详细信息")
public class JobApplicationDTO {
    /**
     * 申请ID
     */
    @ApiModelProperty(value = "申请ID", notes = "唯一标识", example = "1")
    private Long id;
    
    /**
     * 职位ID
     */
    @ApiModelProperty(value = "职位ID", example = "100")
    private Long jobId;
    
    /**
     * 职位标题
     */
    @ApiModelProperty(value = "职位标题", example = "软件工程师")
    private String jobTitle;
    
    /**
     * 申请人ID
     */
    @ApiModelProperty(value = "申请人ID", example = "200")
    private Long userId;
    
    /**
     * 申请人用户名
     */
    @ApiModelProperty(value = "申请人用户名", example = "user123")
    private String username;
    
    /**
     * 申请人真实姓名
     */
    @ApiModelProperty(value = "申请人真实姓名", example = "张三")
    private String realName;
    
    /**
     * 申请人头像
     */
    @ApiModelProperty(value = "申请人头像", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    /**
     * 简历链接
     */
    @ApiModelProperty(value = "简历链接", example = "https://example.com/resume.pdf")
    private String resumeUrl;
    
    /**
     * 求职信/自我介绍
     */
    @ApiModelProperty(value = "求职信", notes = "自我介绍", example = "我是一名有5年经验的开发人员...")
    private String coverLetter;
    
    /**
     * 申请状态
     */
    @ApiModelProperty(value = "申请状态", example = "PENDING")
    private JobApplicationStatus status;
    
    /**
     * 拒绝原因（如果被拒绝）
     */
    @ApiModelProperty(value = "拒绝原因", notes = "如果被拒绝", example = "您的技能与岗位要求不匹配")
    private String rejectionReason;
    
    /**
     * 面试/审核备注
     */
    @ApiModelProperty(value = "面试备注", notes = "面试/审核备注", example = "候选人沟通能力良好")
    private String notes;
    
    /**
     * 面试时间
     */
    @ApiModelProperty(value = "面试时间", example = "2023-01-15 14:00:00")
    private LocalDateTime interviewTime;
    
    /**
     * 面试地点
     */
    @ApiModelProperty(value = "面试地点", example = "北京市海淀区XX大厦5层")
    private String interviewLocation;
    
    /**
     * 是否已读
     */
    @ApiModelProperty(value = "是否已读", example = "true")
    private Boolean isRead;
    
    /**
     * 申请创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "2023-01-01 10:00:00")
    private LocalDateTime createTime;
    
    /**
     * 申请更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "2023-01-02 15:30:00")
    private LocalDateTime updateTime;
} 
package com.thfh.dto;

import com.thfh.model.JobApplicationStatus;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 职位申请数据传输对象
 * 用于前后端数据交换
 */
@Data
public class JobApplicationDTO {
    /**
     * 申请ID
     */
    private Long id;
    
    /**
     * 职位ID
     */
    private Long jobId;
    
    /**
     * 职位标题
     */
    private String jobTitle;
    
    /**
     * 申请人ID
     */
    private Long userId;
    
    /**
     * 申请人用户名
     */
    private String username;
    
    /**
     * 申请人真实姓名
     */
    private String realName;
    
    /**
     * 申请人头像
     */
    private String avatar;
    
    /**
     * 简历链接
     */
    private String resumeUrl;
    
    /**
     * 求职信/自我介绍
     */
    private String coverLetter;
    
    /**
     * 申请状态
     */
    private JobApplicationStatus status;
    
    /**
     * 拒绝原因（如果被拒绝）
     */
    private String rejectionReason;
    
    /**
     * 面试/审核备注
     */
    private String notes;
    
    /**
     * 面试时间
     */
    private LocalDateTime interviewTime;
    
    /**
     * 面试地点
     */
    private String interviewLocation;
    
    /**
     * 是否已读
     */
    private Boolean isRead;
    
    /**
     * 申请创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 申请更新时间
     */
    private LocalDateTime updateTime;
} 
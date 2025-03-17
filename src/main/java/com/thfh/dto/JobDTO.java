package com.thfh.dto;

import com.thfh.model.JobStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 工作岗位数据传输对象
 * 用于在不同层之间传输工作岗位信息
 */
@Data
public class JobDTO {
    /**
     * 工作岗位ID，唯一标识
     */
    private Long id;
    
    /**
     * 工作岗位标题
     */
    private String title;
    
    /**
     * 工作岗位详细描述
     */
    private String description;
    
    /**
     * 发布公司ID
     */
    private Long companyId;
    
    /**
     * 发布公司名称
     */
    private String companyName;
    
    /**
     * 职位分类ID
     */
    private Long categoryId;
    
    /**
     * 职位分类名称
     */
    private String categoryName;
    
    /**
     * 工作地点
     */
    private String location;
    
    /**
     * 薪资范围下限
     */
    private BigDecimal salaryMin;
    
    /**
     * 薪资范围上限
     */
    private BigDecimal salaryMax;
    
    /**
     * 岗位要求
     */
    private String requirements;
    
    /**
     * 工作福利
     */
    private String benefits;
    
    /**
     * 残障人士支持措施
     */
    private String disabilitySupport;
    
    /**
     * 联系人姓名
     */
    private String contactPerson;
    
    /**
     * 联系人电话
     */
    private String contactPhone;
    
    /**
     * 联系人邮箱
     */
    private String contactEmail;
    
    /**
     * 岗位状态（如：草稿、已发布、已关闭等）
     */
    private JobStatus status;
    
    /**
     * 岗位浏览次数
     */
    private Integer viewCount;
    
    /**
     * 岗位申请次数
     */
    private Integer applyCount;
    
    /**
     * 岗位是否启用
     */
    private Boolean enabled;
    
    /**
     * 岗位创建时间
     */
    private LocalDateTime createTime;
} 
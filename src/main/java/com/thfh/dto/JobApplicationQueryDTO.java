package com.thfh.dto;

import com.thfh.model.JobApplicationStatus;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 职位申请查询条件DTO
 * 用于接收前端职位申请列表查询条件
 */
@Data
public class JobApplicationQueryDTO {
    /**
     * 职位ID
     */
    private Long jobId;
    
    /**
     * 公司ID
     */
    private Long companyId;
    
    /**
     * 申请人ID
     */
    private Long userId;
    
    /**
     * 申请状态
     */
    private JobApplicationStatus status;
    
    /**
     * 是否已读
     */
    private Boolean isRead;
    
    /**
     * 开始创建时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束创建时间
     */
    private LocalDateTime endTime;
    
    /**
     * 当前页码，默认为第1页
     */
    private Integer pageNum = 1;
    
    /**
     * 每页记录数，默认为10条
     */
    private Integer pageSize = 10;
} 
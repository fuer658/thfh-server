package com.thfh.dto;

import com.thfh.model.JobApplicationStatus;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;

/**
 * 职位申请查询条件DTO
 * 用于接收前端职位申请列表查询条件
 */
@Data
@ApiModel(value = "职位申请查询参数", description = "用于职位申请列表的筛选条件")
public class JobApplicationQueryDTO {
    /**
     * 职位ID
     */
    @ApiModelProperty(value = "职位ID", example = "1")
    private Long jobId;
    
    /**
     * 公司ID
     */
    @ApiModelProperty(value = "公司ID", example = "100")
    private Long companyId;
    
    /**
     * 申请人ID
     */
    @ApiModelProperty(value = "申请人ID", example = "200")
    private Long userId;
    
    /**
     * 申请状态
     */
    @ApiModelProperty(value = "申请状态", example = "PENDING")
    private JobApplicationStatus status;
    
    /**
     * 是否已读
     */
    @ApiModelProperty(value = "是否已读", example = "true")
    private Boolean isRead;
    
    /**
     * 开始创建时间
     */
    @ApiModelProperty(value = "开始创建时间", example = "2023-01-01 00:00:00")
    private LocalDateTime startTime;
    
    /**
     * 结束创建时间
     */
    @ApiModelProperty(value = "结束创建时间", example = "2023-01-31 23:59:59")
    private LocalDateTime endTime;
    
    /**
     * 当前页码，默认为第1页
     */
    @ApiModelProperty(value = "当前页码", example = "1")
    private Integer pageNum = 1;
    
    /**
     * 每页记录数，默认为10条
     */
    @ApiModelProperty(value = "每页记录数", example = "10")
    private Integer pageSize = 10;
} 
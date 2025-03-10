package com.thfh.dto;

import com.thfh.model.JobStatus;
import lombok.Data;

/**
 * 工作岗位查询数据传输对象
 * 用于接收前端工作岗位列表查询条件
 */
@Data
public class JobQueryDTO {
    /**
     * 岗位标题查询条件（模糊匹配）
     */
    private String title;
    
    /**
     * 公司ID查询条件
     */
    private Long companyId;
    
    /**
     * 工作地点查询条件（模糊匹配）
     */
    private String location;
    
    /**
     * 岗位状态查询条件
     */
    private JobStatus status;
    
    /**
     * 岗位启用状态查询条件
     */
    private Boolean enabled;
    
    /**
     * 当前页码，默认为第1页
     */
    private Integer pageNum = 1;
    
    /**
     * 每页记录数，默认为10条
     */
    private Integer pageSize = 10;
} 
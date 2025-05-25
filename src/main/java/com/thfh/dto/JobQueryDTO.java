package com.thfh.dto;

import com.thfh.model.JobStatus;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 工作岗位查询数据传输对象
 * 用于接收前端工作岗位列表查询条件
 */
@Data
@Schema(description = "工作岗位查询参数 - 用于工作岗位列表的筛选条件")
public class JobQueryDTO {
    /**
     * 岗位标题查询条件（模糊匹配）
     */
    @Schema(description = "岗位标题", description = "模糊匹配", example = "软件开发")
    private String title;
    
    /**
     * 公司ID查询条件
     */
    @Schema(description = "公司ID", example = "1")
    private Long companyId;
    
    /**
     * 公司名称查询条件（模糊匹配）
     */
    @Schema(description = "公司名称", description = "模糊匹配", example = "科技")
    private String companyName;
    
    /**
     * 工作地点查询条件（模糊匹配）
     */
    @Schema(description = "工作地点", description = "模糊匹配", example = "北京")
    private String location;
    
    /**
     * 岗位状态查询条件
     */
    @Schema(description = "岗位状态", example = "PUBLISHED")
    private JobStatus status;
    
    /**
     * 岗位启用状态查询条件
     */
    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;
    
    /**
     * 职位分类ID查询条件
     */
    @Schema(description = "职位分类ID", example = "5")
    private Long categoryId;
    
    /**
     * 当前页码，默认为第1页
     */
    @Schema(description = "当前页码", example = "1")
    private Integer pageNum = 1;
    
    /**
     * 每页记录数，默认为10条
     */
    @Schema(description = "每页记录数", example = "10")
    private Integer pageSize = 10;
} 
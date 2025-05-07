package com.thfh.dto;

import com.thfh.model.CourseStatus;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 课程查询数据传输对象
 * 用于接收前端课程列表查询条件
 */
@Data
@ApiModel(value = "课程查询参数", description = "用于课程列表的筛选条件")
public class CourseQueryDTO {
    /**
     * 课程标题查询条件（模糊匹配）
     */
    @ApiModelProperty(value = "课程标题", notes = "模糊匹配", example = "Java")
    private String title;
    
    /**
     * 讲师ID查询条件
     */
    @ApiModelProperty(value = "讲师ID", example = "1")
    private Long teacherId;
    
    /**
     * 课程状态查询条件
     */
    @ApiModelProperty(value = "课程状态", example = "PUBLISHED")
    private CourseStatus status;
    
    /**
     * 课程启用状态查询条件
     */
    @ApiModelProperty(value = "是否启用", example = "true")
    private Boolean enabled;
    
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
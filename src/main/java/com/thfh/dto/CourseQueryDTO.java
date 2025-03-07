package com.thfh.dto;

import com.thfh.model.CourseStatus;
import lombok.Data;

/**
 * 课程查询数据传输对象
 * 用于接收前端课程列表查询条件
 */
@Data
public class CourseQueryDTO {
    /**
     * 课程标题查询条件（模糊匹配）
     */
    private String title;
    
    /**
     * 讲师ID查询条件
     */
    private Long teacherId;
    
    /**
     * 课程状态查询条件
     */
    private CourseStatus status;
    
    /**
     * 课程启用状态查询条件
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
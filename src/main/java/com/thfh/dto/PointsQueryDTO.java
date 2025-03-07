package com.thfh.dto;

import com.thfh.model.PointsType;
import lombok.Data;

/**
 * 积分记录查询数据传输对象
 * 用于接收前端积分记录列表查询条件
 */
@Data
public class PointsQueryDTO {
    /**
     * 学生/用户ID查询条件
     */
    private Long studentId;
    
    /**
     * 积分类型查询条件
     */
    private PointsType type;
    
    /**
     * 当前页码，默认为第1页
     */
    private Integer pageNum = 1;
    
    /**
     * 每页记录数，默认为10条
     */
    private Integer pageSize = 10;
} 
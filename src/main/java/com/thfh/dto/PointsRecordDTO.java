package com.thfh.dto;

import com.thfh.model.PointsType;
import lombok.Data;

/**
 * 积分记录数据传输对象
 * 用于在不同层之间传输积分记录信息
 */
@Data
public class PointsRecordDTO {
    /**
     * 积分记录ID，唯一标识
     */
    private Long id;
    
    /**
     * 学生/用户ID
     */
    private Long studentId;
    
    /**
     * 学生/用户姓名
     */
    private String studentName;
    
    /**
     * 积分变动数量（正数为增加，负数为减少）
     */
    private Integer points;
    
    /**
     * 积分变动类型（如：课程购买、任务完成、管理员调整等）
     */
    private PointsType type;
    
    /**
     * 积分变动说明/原因
     */
    private String description;
    
    /**
     * 积分记录创建时间
     */
    private String createTime;
} 
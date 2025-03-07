package com.thfh.dto;

import lombok.Data;

/**
 * 积分调整数据传输对象
 * 用于管理员调整用户积分时传递相关信息
 */
@Data
public class PointsAdjustDTO {
    /**
     * 学生/用户ID
     */
    private Long studentId;
    
    /**
     * 调整的积分数量（正数为增加，负数为减少）
     */
    private Integer points;
    
    /**
     * 积分调整说明/原因
     */
    private String description;
} 
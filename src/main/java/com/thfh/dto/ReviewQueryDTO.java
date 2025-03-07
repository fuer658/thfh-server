package com.thfh.dto;

import com.thfh.model.ReviewType;
import lombok.Data;

/**
 * 评价查询数据传输对象
 * 用于接收前端评价列表查询条件
 */
@Data
public class ReviewQueryDTO {
    /**
     * 评价类型查询条件
     */
    private ReviewType type;
    
    /**
     * 评价目标ID查询条件
     */
    private Long targetId;
    
    /**
     * 评价用户ID查询条件
     */
    private Long userId;
    
    /**
     * 评分查询条件（1-5星）
     */
    private Integer rating;
    
    /**
     * 评价启用状态查询条件
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
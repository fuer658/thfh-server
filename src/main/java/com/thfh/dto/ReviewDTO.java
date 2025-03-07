package com.thfh.dto;

import com.thfh.model.ReviewType;
import lombok.Data;

/**
 * 评价数据传输对象
 * 用于在不同层之间传输评价信息
 */
@Data
public class ReviewDTO {
    /**
     * 评价ID，唯一标识
     */
    private Long id;
    
    /**
     * 评价类型（如：课程评价、作品评价等）
     */
    private ReviewType type;
    
    /**
     * 评价目标ID（课程ID或作品ID等）
     */
    private Long targetId;
    
    /**
     * 评价目标名称（课程名/作品名）
     */
    private String targetName;
    
    /**
     * 评价用户ID
     */
    private Long userId;
    
    /**
     * 评价用户名称
     */
    private String userName;
    
    /**
     * 评分（1-5星）
     */
    private Integer rating;
    
    /**
     * 评价内容
     */
    private String content;
    
    /**
     * 评价附带图片，多张图片以逗号分隔
     */
    private String images;
    
    /**
     * 评价是否启用
     */
    private Boolean enabled;
    
    /**
     * 评价创建时间
     */
    private String createTime;
} 
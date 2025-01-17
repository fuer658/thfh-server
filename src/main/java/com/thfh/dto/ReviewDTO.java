package com.thfh.dto;

import com.thfh.model.ReviewType;
import lombok.Data;

@Data
public class ReviewDTO {
    private Long id;
    private ReviewType type;
    private Long targetId;
    private String targetName; // 评价目标名称（课程名/作品名）
    private Long userId;
    private String userName;
    private Integer rating;
    private String content;
    private String images;
    private Boolean enabled;
    private String createTime;
} 
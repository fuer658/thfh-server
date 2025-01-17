package com.thfh.dto;

import com.thfh.model.ReviewType;
import lombok.Data;

@Data
public class ReviewQueryDTO {
    private ReviewType type;
    private Long targetId;
    private Long userId;
    private Integer rating;
    private Boolean enabled;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
} 
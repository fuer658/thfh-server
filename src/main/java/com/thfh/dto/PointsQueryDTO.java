package com.thfh.dto;

import com.thfh.model.PointsType;
import lombok.Data;

@Data
public class PointsQueryDTO {
    private Long studentId;
    private PointsType type;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
} 
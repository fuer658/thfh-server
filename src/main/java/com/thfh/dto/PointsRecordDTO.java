package com.thfh.dto;

import com.thfh.model.PointsType;
import lombok.Data;

@Data
public class PointsRecordDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Integer points;
    private PointsType type;
    private String description;
    private String createTime;
} 
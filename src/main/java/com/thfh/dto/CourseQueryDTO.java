package com.thfh.dto;

import com.thfh.model.CourseStatus;
import lombok.Data;

@Data
public class CourseQueryDTO {
    private String title;
    private Long teacherId;
    private CourseStatus status;
    private Boolean enabled;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
} 
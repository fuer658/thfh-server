package com.thfh.dto;

import com.thfh.model.WorkStatus;
import lombok.Data;

@Data
public class WorkQueryDTO {
    private String title;
    private Long studentId;
    private WorkStatus status;
    private Boolean enabled;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
} 
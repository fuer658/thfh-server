package com.thfh.dto;

import com.thfh.model.JobStatus;
import lombok.Data;

@Data
public class JobQueryDTO {
    private String title;
    private Long companyId;
    private String location;
    private JobStatus status;
    private Boolean enabled;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
} 
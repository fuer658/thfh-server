package com.thfh.dto;

import com.thfh.model.JobStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class JobDTO {
    private Long id;
    private String title;
    private String description;
    private Long companyId;
    private String companyName;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String requirements;
    private String benefits;
    private String disabilitySupport;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private JobStatus status;
    private Integer viewCount;
    private Integer applyCount;
    private Boolean enabled;
    private LocalDateTime createTime;
} 
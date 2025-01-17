package com.thfh.dto;

import com.thfh.model.WorkStatus;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class WorkDTO {
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private Long studentId;
    private String studentName;
    private BigDecimal price;
    private WorkStatus status;
    private String images;
    private String video;
    private String materials;
    private Integer likeCount;
    private Integer viewCount;
    private Integer saleCount;
    private Boolean enabled;
    private String createTime;
} 
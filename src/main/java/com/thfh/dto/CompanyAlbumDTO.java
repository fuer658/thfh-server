package com.thfh.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CompanyAlbumDTO {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private Long companyId;
    private Long categoryId;
    private String categoryName;
    private Integer sortOrder;
    private Boolean enabled;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 
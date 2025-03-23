package com.thfh.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CompanyAlbumCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Long companyId;
    private Boolean enabled;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 
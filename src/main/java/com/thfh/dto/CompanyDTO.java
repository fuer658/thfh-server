package com.thfh.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CompanyDTO {
    private Long id;
    private String name;
    private String logo;
    private String description;
    private String industry;
    private String scale;
    private String website;
    private String address;
    private Boolean enabled;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

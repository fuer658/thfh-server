package com.thfh.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Company {
    private Long id;
    private String name;            // 公司名称
    private String logo;            // 公司logo
    private String description;     // 公司描述
    private String industry;        // 所属行业
    private String scale;           // 公司规模
    private String website;         // 公司网站
    private String address;         // 公司地址
    private Boolean enabled;        // 是否启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
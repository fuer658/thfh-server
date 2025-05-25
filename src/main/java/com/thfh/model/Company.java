package com.thfh.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Entity
@Table(name = "company")
@Schema(description = "公司实体类")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "公司ID", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "公司名称", required = true)
    private String name; // 公司名称

    @Schema(description = "公司logo的URL", example = "http://example.com/logo.png")
    private String logo; // 公司logo

    @Column(length = 1000)
    @Schema(description = "公司简介 - 最多1000字符")
    private String description; // 公司简介

    @Schema(description = "所属行业", example = "IT")
    private String industry; // 所属行业

    @Schema(description = "公司规模", example = "100-500人")
    private String scale; // 公司规模

    @Schema(description = "公司网站", example = "https://www.example.com")
    private String website; // 公司网站

    @Schema(description = "公司地址")
    private String address; // 公司地址

    @Column(nullable = false)
    @Schema(description = "是否启用", example = "true", required = true)
    private Boolean enabled = true;

    @Column(nullable = false)
    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    @Schema(description = "更新时间", required = true)
    private LocalDateTime updateTime = LocalDateTime.now();

    @Column(length = 500)
    @Schema(description = "公司标签 - 多个标签以逗号分隔", example = "互联网,科技,教育")
    private String tags; // 公司标签，多个标签以逗号分隔

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("company")
    @Schema(description = "相册分类列表")
    private List<CompanyAlbumCategory> albumCategories;
}
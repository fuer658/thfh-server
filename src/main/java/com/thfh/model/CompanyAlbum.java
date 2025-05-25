package com.thfh.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Entity
@Table(name = "company_album")
@Schema(description = "公司相册实体类")
public class CompanyAlbum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "相册ID", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "相册标题", required = true)
    private String title; // 相册标题

    @Column(length = 500)
    @Schema(description = "相册描述 - 最多500字符")
    private String description; // 相册描述

    @Column(nullable = false)
    @Schema(description = "图片URL", required = true, example = "http://example.com/image.jpg")
    private String imageUrl; // 图片URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @Schema(description = "所属公司")
    private Company company; // 所属公司

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    @Schema(description = "所属分类")
    private CompanyAlbumCategory category; // 所属分类

    @Column(nullable = false)
    @Schema(description = "排序顺序", example = "0", required = true)
    private Integer sortOrder = 0; // 排序顺序

    @Column(nullable = false)
    @Schema(description = "是否启用", example = "true", required = true)
    private Boolean enabled = true; // 是否启用

    @Column(nullable = false)
    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    @Schema(description = "更新时间", required = true)
    private LocalDateTime updateTime = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 
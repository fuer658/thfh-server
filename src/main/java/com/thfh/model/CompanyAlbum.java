package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "company_album")
public class CompanyAlbum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 相册标题

    @Column(length = 500)
    private String description; // 相册描述

    @Column(nullable = false)
    private String imageUrl; // 图片URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // 所属公司

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    private CompanyAlbumCategory category; // 所属分类

    @Column(nullable = false)
    private Integer sortOrder = 0; // 排序顺序

    @Column(nullable = false)
    private Boolean enabled = true; // 是否启用

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 
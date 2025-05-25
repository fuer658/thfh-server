package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 职位分类实体类
 * 用于对职位进行分类管理
 */
@Data
@Entity
@Table(name = "job_category")
@Schema(description = "职位分类 - 用于对职位进行分类管理")
public class JobCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "分类ID", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "分类名称", required = true, example = "技术开发")
    private String name; // 分类名称

    @Column(length = 500)
    @Schema(description = "分类描述", example = "包含各类技术开发相关的职位")
    private String description; // 分类描述

    @Column(name = "parent_id")
    @Schema(description = "父分类ID - 如果为null则表示是顶级分类", example = "0")
    private Long parentId; // 父分类ID，如果为null则表示是顶级分类

    @Column(nullable = false)
    @Schema(description = "排序值 - 值越小越靠前", example = "0")
    private Integer sort = 0; // 排序字段，值越小越靠前

    @Column(nullable = false)
    @Schema(description = "是否启用", example = "true")
    private Boolean enabled = true; // 是否启用

    @Column(nullable = false)
    @Schema(description = "创建时间", example = "2023-05-20T14:30:00")
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    @Schema(description = "更新时间", example = "2023-05-20T14:30:00")
    private LocalDateTime updateTime = LocalDateTime.now();

    // 子分类列表，不映射到数据库
    @Transient
    @Schema(description = "子分类列表 - 不映射到数据库，用于前端树形展示")
    private List<JobCategory> children = new ArrayList<>();

    /**
     * 更新实体的updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 
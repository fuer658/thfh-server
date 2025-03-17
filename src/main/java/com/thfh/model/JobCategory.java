package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
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
public class JobCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 分类名称

    @Column(length = 500)
    private String description; // 分类描述

    @Column(name = "parent_id")
    private Long parentId; // 父分类ID，如果为null则表示是顶级分类

    @Column(nullable = false)
    private Integer sort = 0; // 排序字段，值越小越靠前

    @Column(nullable = false)
    private Boolean enabled = true; // 是否启用

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now();

    // 子分类列表，不映射到数据库
    @Transient
    private List<JobCategory> children = new ArrayList<>();

    /**
     * 更新实体的updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 
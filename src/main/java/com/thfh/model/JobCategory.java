package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "职位分类", description = "用于对职位进行分类管理")
public class JobCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "分类ID", example = "1", position = 1)
    private Long id;

    @Column(nullable = false)
    @ApiModelProperty(value = "分类名称", required = true, example = "技术开发", position = 2)
    private String name; // 分类名称

    @Column(length = 500)
    @ApiModelProperty(value = "分类描述", example = "包含各类技术开发相关的职位", position = 3)
    private String description; // 分类描述

    @Column(name = "parent_id")
    @ApiModelProperty(value = "父分类ID", notes = "如果为null则表示是顶级分类", example = "0", position = 4)
    private Long parentId; // 父分类ID，如果为null则表示是顶级分类

    @Column(nullable = false)
    @ApiModelProperty(value = "排序值", notes = "值越小越靠前", example = "0", position = 5)
    private Integer sort = 0; // 排序字段，值越小越靠前

    @Column(nullable = false)
    @ApiModelProperty(value = "是否启用", example = "true", position = 6)
    private Boolean enabled = true; // 是否启用

    @Column(nullable = false)
    @ApiModelProperty(value = "创建时间", example = "2023-05-20T14:30:00", position = 7)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    @ApiModelProperty(value = "更新时间", example = "2023-05-20T14:30:00", position = 8)
    private LocalDateTime updateTime = LocalDateTime.now();

    // 子分类列表，不映射到数据库
    @Transient
    @ApiModelProperty(value = "子分类列表", notes = "不映射到数据库，用于前端树形展示", position = 9)
    private List<JobCategory> children = new ArrayList<>();

    /**
     * 更新实体的updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 
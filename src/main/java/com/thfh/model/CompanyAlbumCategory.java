package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@Entity
@Table(name = "company_album_category")
@ApiModel(description = "公司相册分类实体类")
public class CompanyAlbumCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "分类ID", example = "1")
    private Long id;

    @Column(nullable = false)
    @ApiModelProperty(value = "分类名称", required = true)
    private String name; // 分类名称

    @Column(length = 500)
    @ApiModelProperty(value = "分类描述", notes = "最多500字符")
    private String description; // 分类描述

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @ApiModelProperty(value = "所属公司")
    private Company company; // 所属公司

    @Column(nullable = false)
    @ApiModelProperty(value = "是否启用", example = "true", required = true)
    private Boolean enabled = true; // 是否启用

    @Column(nullable = false)
    @ApiModelProperty(value = "排序顺序", example = "0", required = true)
    private Integer sortOrder = 0; // 排序顺序

    @Column(nullable = false)
    @ApiModelProperty(value = "创建时间", required = true)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    @ApiModelProperty(value = "更新时间", required = true)
    private LocalDateTime updateTime = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 
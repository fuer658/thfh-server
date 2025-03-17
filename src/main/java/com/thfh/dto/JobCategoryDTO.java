package com.thfh.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 职位分类数据传输对象
 * 用于在不同层之间传输职位分类信息
 */
@Data
public class JobCategoryDTO {
    /**
     * 分类ID
     */
    private Long id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类描述
     */
    private String description;
    
    /**
     * 父分类ID
     */
    private Long parentId;
    
    /**
     * 排序值
     */
    private Integer sort;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 子分类列表
     */
    private List<JobCategoryDTO> children = new ArrayList<>();
} 
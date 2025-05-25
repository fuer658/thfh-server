package com.thfh.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 职位分类数据传输对象
 * 用于在不同层之间传输职位分类信息
 */
@Data
@Schema(description = "职位分类信息 - 包含职位分类的详细信息")
public class JobCategoryDTO {
    /**
     * 分类ID
     */
    @Schema(description = "分类ID", example = "1")
    private Long id;
    
    /**
     * 分类名称
     */
    @Schema(description = "分类名称", required = true, example = "技术开发")
    private String name;
    
    /**
     * 分类描述
     */
    @Schema(description = "分类描述", example = "包含各类技术开发岗位")
    private String description;
    
    /**
     * 父分类ID
     */
    @Schema(description = "父分类ID", description = "顶级分类的父ID为0或null", example = "0")
    private Long parentId;
    
    /**
     * 排序值
     */
    @Schema(description = "排序值", description = "数值越小越靠前", example = "1")
    private Integer sort;
    
    /**
     * 是否启用
     */
    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2023-01-01 10:00:00")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2023-01-15 15:30:00")
    private LocalDateTime updateTime;
    
    /**
     * 子分类列表
     */
    @Schema(description = "子分类列表 - 当前分类的子分类集合")
    private List<JobCategoryDTO> children = new ArrayList<>();
} 
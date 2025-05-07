package com.thfh.dto;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 职位分类数据传输对象
 * 用于在不同层之间传输职位分类信息
 */
@Data
@ApiModel(value = "职位分类信息", description = "包含职位分类的详细信息")
public class JobCategoryDTO {
    /**
     * 分类ID
     */
    @ApiModelProperty(value = "分类ID", notes = "唯一标识", example = "1")
    private Long id;
    
    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称", required = true, example = "技术开发")
    private String name;
    
    /**
     * 分类描述
     */
    @ApiModelProperty(value = "分类描述", example = "包含各类技术开发岗位")
    private String description;
    
    /**
     * 父分类ID
     */
    @ApiModelProperty(value = "父分类ID", notes = "顶级分类的父ID为0或null", example = "0")
    private Long parentId;
    
    /**
     * 排序值
     */
    @ApiModelProperty(value = "排序值", notes = "数值越小越靠前", example = "1")
    private Integer sort;
    
    /**
     * 是否启用
     */
    @ApiModelProperty(value = "是否启用", example = "true")
    private Boolean enabled;
    
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "2023-01-01 10:00:00")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "2023-01-15 15:30:00")
    private LocalDateTime updateTime;
    
    /**
     * 子分类列表
     */
    @ApiModelProperty(value = "子分类列表", notes = "当前分类的子分类集合")
    private List<JobCategoryDTO> children = new ArrayList<>();
} 
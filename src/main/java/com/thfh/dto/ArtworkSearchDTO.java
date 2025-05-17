package com.thfh.dto;

import com.thfh.model.ArtworkType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 作品动态搜索条件DTO
 */
@Data
@ApiModel(value = "作品搜索条件", description = "用于动态搜索作品的条件")
public class ArtworkSearchDTO {

    @ApiModelProperty(value = "关键词（匹配标题、描述、创作材料）", example = "水彩")
    private String keyword;

    @ApiModelProperty(value = "标签ID列表", example = "[1, 2, 3]")
    private List<Long> tagIds;

    @ApiModelProperty(value = "作品类型", example = "PERSONAL")
    private ArtworkType type;

    @ApiModelProperty(value = "创建者ID列表", example = "[101, 102]")
    private List<Long> creatorIds;

    @ApiModelProperty(value = "最低评分", example = "3.5")
    private BigDecimal minScore;

    @ApiModelProperty(value = "最高评分", example = "5.0")
    private BigDecimal maxScore;

    @ApiModelProperty(value = "最低价格", example = "100")
    private BigDecimal minPrice;

    @ApiModelProperty(value = "最高价格", example = "1000")
    private BigDecimal maxPrice;

    @ApiModelProperty(value = "是否推荐", example = "true")
    private Boolean recommended;

    @ApiModelProperty(value = "是否启用", example = "true")
    private Boolean enabled = true;

    @ApiModelProperty(value = "排序字段", example = "createTime")
    private String sortField = "createTime";

    @ApiModelProperty(value = "排序方向(asc, desc)", example = "desc")
    private String sortDirection = "desc";
} 
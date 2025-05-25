package com.thfh.dto;

import com.thfh.model.ArtworkType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 作品动态搜索条件DTO
 */
@Data
@Schema(description = "作品搜索条件 - 用于动态搜索作品的条件")
public class ArtworkSearchDTO {

    @Schema(description = "关键词（匹配标题、描述、创作材料）", example = "水彩")
    private String keyword;

    @Schema(description = "标签ID列表", example = "[1, 2, 3]")
    private List<Long> tagIds;

    @Schema(description = "作品类型", example = "PERSONAL")
    private ArtworkType type;

    @Schema(description = "创建者ID列表", example = "[101, 102]")
    private List<Long> creatorIds;

    @Schema(description = "最低评分", example = "3.5")
    private BigDecimal minScore;

    @Schema(description = "最高评分", example = "5.0")
    private BigDecimal maxScore;

    @Schema(description = "最低价格", example = "100")
    private BigDecimal minPrice;

    @Schema(description = "最高价格", example = "1000")
    private BigDecimal maxPrice;

    @Schema(description = "是否推荐", example = "true")
    private Boolean recommended;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled = true;

    @Schema(description = "排序字段", example = "createTime")
    private String sortField = "createTime";

    @Schema(description = "排序方向(asc, desc)", example = "desc")
    private String sortDirection = "desc";
} 
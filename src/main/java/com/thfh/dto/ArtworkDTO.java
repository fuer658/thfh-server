package com.thfh.dto;

import com.thfh.model.ArtworkType;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

/**
 * 艺术作品数据传输对象
 */
@Data
@Schema(description = "艺术作品信息 - 包含艺术作品的详细信息")
public class ArtworkDTO {
    @Schema(description = "作品ID", description = "唯一标识", example = "1")
    private Long id;
    
    @Schema(description = "作品标题", required = true, example = "彩虹之光")
    private String title;
    
    @Schema(description = "作品描述", example = "这是一幅描绘彩虹的画作")
    private String description;
    
    @Schema(description = "封面URL", example = "https://example.com/artwork.jpg")
    private String coverUrl;
    
    @Schema(description = "材料", example = "水彩、油画颜料")
    private String materials;
    
    @Schema(description = "价格", example = "299.99")
    private BigDecimal price;
    
    @Schema(description = "作品类型", example = "PAINTING")
    private ArtworkType type;
    
    // 创建者信息
    @Schema(description = "创建者ID", example = "100")
    private Long creatorId;
    
    @Schema(description = "创建者名称", example = "张三")
    private String creatorName;
    
    @Schema(description = "创建者头像", example = "https://example.com/avatar.jpg")
    private String creatorAvatar;
    
    @Schema(description = "标签集合")
    private Set<TagDTO> tags;
    
    @Schema(description = "是否推荐", example = "true")
    private Boolean recommended;
    
    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;
    
    @Schema(description = "平均评分", example = "4.8")
    private BigDecimal averageScore;
    
    @Schema(description = "评分次数", example = "56")
    private Integer scoreCount;
    
    @Schema(description = "收藏次数", example = "120")
    private Integer favoriteCount;
    
    @Schema(description = "点赞次数", example = "230")
    private Integer likeCount;
    
    @Schema(description = "浏览量", example = "450")
    private Integer viewCount;
    
    @Schema(description = "创建时间", example = "2023-01-01 10:00:00")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间", example = "2023-01-15 15:30:00")
    private LocalDateTime updateTime;
    
    @Schema(description = "作品图册列表")
    private List<ArtworkGalleryDTO> galleries;
} 
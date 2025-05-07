package com.thfh.dto;

import com.thfh.model.ArtworkType;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 艺术作品数据传输对象
 */
@Data
@ApiModel(value = "艺术作品信息", description = "包含艺术作品的详细信息")
public class ArtworkDTO {
    @ApiModelProperty(value = "作品ID", notes = "唯一标识", example = "1")
    private Long id;
    
    @ApiModelProperty(value = "作品标题", required = true, example = "彩虹之光")
    private String title;
    
    @ApiModelProperty(value = "作品描述", example = "这是一幅描绘彩虹的画作")
    private String description;
    
    @ApiModelProperty(value = "封面URL", example = "https://example.com/artwork.jpg")
    private String coverUrl;
    
    @ApiModelProperty(value = "材料", example = "水彩、油画颜料")
    private String materials;
    
    @ApiModelProperty(value = "价格", example = "299.99")
    private BigDecimal price;
    
    @ApiModelProperty(value = "作品类型", example = "PAINTING")
    private ArtworkType type;
    
    // 创建者信息
    @ApiModelProperty(value = "创建者ID", example = "100")
    private Long creatorId;
    
    @ApiModelProperty(value = "创建者名称", example = "张三")
    private String creatorName;
    
    @ApiModelProperty(value = "创建者头像", example = "https://example.com/avatar.jpg")
    private String creatorAvatar;
    
    @ApiModelProperty(value = "标签集合")
    private Set<TagDTO> tags;
    
    @ApiModelProperty(value = "是否推荐", example = "true")
    private Boolean recommended;
    
    @ApiModelProperty(value = "是否启用", example = "true")
    private Boolean enabled;
    
    @ApiModelProperty(value = "平均评分", example = "4.8")
    private BigDecimal averageScore;
    
    @ApiModelProperty(value = "评分次数", example = "56")
    private Integer scoreCount;
    
    @ApiModelProperty(value = "收藏次数", example = "120")
    private Integer favoriteCount;
    
    @ApiModelProperty(value = "点赞次数", example = "230")
    private Integer likeCount;
    
    @ApiModelProperty(value = "创建时间", example = "2023-01-01 10:00:00")
    private LocalDateTime createTime;
    
    @ApiModelProperty(value = "更新时间", example = "2023-01-15 15:30:00")
    private LocalDateTime updateTime;
} 
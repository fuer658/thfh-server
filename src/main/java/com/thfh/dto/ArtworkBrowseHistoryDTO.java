package com.thfh.dto;

import com.thfh.model.ArtworkType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@ApiModel(value = "作品浏览记录DTO", description = "包含作品浏览记录的基本信息")
public class ArtworkBrowseHistoryDTO {
    @ApiModelProperty(value = "浏览记录ID", example = "1")
    private Long historyId;
    
    @ApiModelProperty(value = "作品ID", example = "1")
    private Long artworkId;
    
    @ApiModelProperty(value = "作品标题", example = "春天的色彩")
    private String title;
    
    @ApiModelProperty(value = "作品描述", example = "这是一幅描绘春天景色的油画作品")
    private String description;
    
    @ApiModelProperty(value = "封面URL", example = "http://example.com/images/artwork.jpg")
    private String coverUrl;
    
    @ApiModelProperty(value = "作品类型", example = "PERSONAL")
    private ArtworkType type;
    
    @ApiModelProperty(value = "创作者ID", example = "100")
    private Long creatorId;
    
    @ApiModelProperty(value = "创作者名称", example = "张三")
    private String creatorName;
    
    @ApiModelProperty(value = "创作者头像", example = "http://example.com/avatar.jpg")
    private String creatorAvatar;
    
    @ApiModelProperty(value = "平均评分", example = "4.8")
    private BigDecimal averageScore;
    
    @ApiModelProperty(value = "浏览量", example = "450")
    private Integer viewCount;
    
    @ApiModelProperty(value = "标签集合")
    private Set<TagDTO> tags;
    
    @ApiModelProperty(value = "最后浏览时间", example = "2023-05-02T14:30:00")
    private LocalDateTime lastBrowseTime;
    
    @ApiModelProperty(value = "浏览次数", example = "5")
    private Integer browseCount;
} 
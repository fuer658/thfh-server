package com.thfh.dto;

import com.thfh.model.ArtworkType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(description = "作品浏览记录DTO - 包含作品浏览记录的基本信息")
public class ArtworkBrowseHistoryDTO {
    @Schema(description = "浏览记录ID", example = "1")
    private Long historyId;
    
    @Schema(description = "作品ID", example = "1")
    private Long artworkId;
    
    @Schema(description = "作品标题", example = "春天的色彩")
    private String title;
    
    @Schema(description = "作品描述", example = "这是一幅描绘春天景色的油画作品")
    private String description;
    
    @Schema(description = "封面URL", example = "http://example.com/images/artwork.jpg")
    private String coverUrl;
    
    @Schema(description = "作品类型", example = "PERSONAL")
    private ArtworkType type;
    
    @Schema(description = "创作者ID", example = "100")
    private Long creatorId;
    
    @Schema(description = "创作者名称", example = "张三")
    private String creatorName;
    
    @Schema(description = "创作者头像", example = "http://example.com/avatar.jpg")
    private String creatorAvatar;
    
    @Schema(description = "平均评分", example = "4.8")
    private BigDecimal averageScore;
    
    @Schema(description = "浏览量", example = "450")
    private Integer viewCount;
    
    @Schema(description = "标签集合")
    private Set<TagDTO> tags;
    
    @Schema(description = "最后浏览时间", example = "2023-05-02T14:30:00")
    private LocalDateTime lastBrowseTime;
    
    @Schema(description = "浏览次数", example = "5")
    private Integer browseCount;
} 
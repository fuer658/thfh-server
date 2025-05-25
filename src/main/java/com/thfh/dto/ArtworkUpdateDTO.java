package com.thfh.dto;

import com.thfh.model.ArtworkTag;
import com.thfh.model.ArtworkType;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

/**
 * 作品更新DTO
 * 用于接收管理员编辑作品的请求
 */
@Data
@Schema(description = "作品更新信息 - 用于管理员编辑作品的请求参数")
public class ArtworkUpdateDTO {
    
    @NotBlank(message = "作品标题不能为空")
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
    
    @NotNull(message = "作品类型不能为空")
    @Schema(description = "作品类型", required = true, example = "PAINTING")
    private ArtworkType type;
    
    @Schema(description = "标签集合")
    private Set<ArtworkTag> tags;
    
    @Schema(description = "是否推荐", example = "true")
    private Boolean recommended;
    
    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;
} 
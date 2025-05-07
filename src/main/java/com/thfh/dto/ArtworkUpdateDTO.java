package com.thfh.dto;

import com.thfh.model.ArtworkTag;
import com.thfh.model.ArtworkType;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

/**
 * 作品更新DTO
 * 用于接收管理员编辑作品的请求
 */
@Data
@ApiModel(value = "作品更新信息", description = "用于管理员编辑作品的请求参数")
public class ArtworkUpdateDTO {
    
    @NotBlank(message = "作品标题不能为空")
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
    
    @NotNull(message = "作品类型不能为空")
    @ApiModelProperty(value = "作品类型", required = true, example = "PAINTING")
    private ArtworkType type;
    
    @ApiModelProperty(value = "标签集合")
    private Set<ArtworkTag> tags;
    
    @ApiModelProperty(value = "是否推荐", example = "true")
    private Boolean recommended;
    
    @ApiModelProperty(value = "是否启用", example = "true")
    private Boolean enabled;
} 
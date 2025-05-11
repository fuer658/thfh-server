package com.thfh.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 作品图册数据传输对象
 */
@Data
@ApiModel(value = "作品图册信息", description = "用于API输出的作品图册数据")
public class ArtworkGalleryDTO {
    
    @ApiModelProperty(value = "图册ID", example = "1")
    private Long id;
    
    @ApiModelProperty(value = "图片URL", example = "http://example.com/image.jpg")
    private String imageUrl;
    
    @ApiModelProperty(value = "图片描述", example = "作品细节展示")
    private String description;
    
    @ApiModelProperty(value = "排序序号", example = "1")
    private Integer sortOrder;
    
    @ApiModelProperty(value = "创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createTime;
    
    @ApiModelProperty(value = "更新时间", example = "2024-01-01 12:00:00")
    private LocalDateTime updateTime;
} 
package com.thfh.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 作品图册数据传输对象
 */
@Data
@Schema(description = "作品图册信息 - 用于API输出的作品图册数据")
public class ArtworkGalleryDTO {
    
    @Schema(description = "图册ID", example = "1")
    private Long id;
    
    @Schema(description = "图片URL", example = "http://example.com/image.jpg")
    private String imageUrl;
    
    @Schema(description = "图片描述", example = "作品细节展示")
    private String description;
    
    @Schema(description = "排序序号", example = "1")
    private Integer sortOrder;
    
    @Schema(description = "创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间", example = "2024-01-01 12:00:00")
    private LocalDateTime updateTime;
} 
package com.thfh.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * 企业相册数据传输对象
 */
@Data
@Schema(description = "企业相册信息 - 包含企业相册的详细信息")
public class CompanyAlbumDTO {
    @Schema(description = "相册ID - 唯一标识", example = "1")
    private Long id;
    
    @Schema(description = "标题", required = true, example = "公司环境展示")
    private String title;
    
    @Schema(description = "描述", example = "展示公司工作环境和设施")
    private String description;
    
    @Schema(description = "图片URL", example = "https://example.com/album.jpg")
    private String imageUrl;
    
    @Schema(description = "公司ID", example = "100")
    private Long companyId;
    
    @Schema(description = "分类ID", example = "5")
    private Long categoryId;
    
    @Schema(description = "分类名称", example = "办公环境")
    private String categoryName;
    
    @Schema(description = "排序顺序", example = "1")
    private Integer sortOrder;
    
    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;
    
    @Schema(description = "创建时间", example = "2023-01-01 10:00:00")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间", example = "2023-01-15 15:30:00")
    private LocalDateTime updateTime;
} 
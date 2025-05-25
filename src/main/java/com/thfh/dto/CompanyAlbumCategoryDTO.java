package com.thfh.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * 企业相册分类数据传输对象
 */
@Data
@Schema(description = "企业相册分类信息 - 包含企业相册分类的详细信息")
public class CompanyAlbumCategoryDTO {
    @Schema(description = "分类ID - 唯一标识", example = "1")
    private Long id;
    
    @Schema(description = "分类名称", required = true, example = "办公环境")
    private String name;
    
    @Schema(description = "分类描述", example = "公司办公环境相关照片")
    private String description;
    
    @Schema(description = "公司ID", example = "100")
    private Long companyId;
    
    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;
    
    @Schema(description = "创建时间", example = "2023-01-01 10:00:00")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间", example = "2023-01-15 15:30:00")
    private LocalDateTime updateTime;
} 
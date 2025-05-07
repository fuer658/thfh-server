package com.thfh.dto;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;

/**
 * 企业相册数据传输对象
 */
@Data
@ApiModel(value = "企业相册信息", description = "包含企业相册的详细信息")
public class CompanyAlbumDTO {
    @ApiModelProperty(value = "相册ID", notes = "唯一标识", example = "1")
    private Long id;
    
    @ApiModelProperty(value = "标题", required = true, example = "公司环境展示")
    private String title;
    
    @ApiModelProperty(value = "描述", example = "展示公司工作环境和设施")
    private String description;
    
    @ApiModelProperty(value = "图片URL", example = "https://example.com/album.jpg")
    private String imageUrl;
    
    @ApiModelProperty(value = "公司ID", example = "100")
    private Long companyId;
    
    @ApiModelProperty(value = "分类ID", example = "5")
    private Long categoryId;
    
    @ApiModelProperty(value = "分类名称", example = "办公环境")
    private String categoryName;
    
    @ApiModelProperty(value = "排序顺序", example = "1")
    private Integer sortOrder;
    
    @ApiModelProperty(value = "是否启用", example = "true")
    private Boolean enabled;
    
    @ApiModelProperty(value = "创建时间", example = "2023-01-01 10:00:00")
    private LocalDateTime createTime;
    
    @ApiModelProperty(value = "更新时间", example = "2023-01-15 15:30:00")
    private LocalDateTime updateTime;
} 
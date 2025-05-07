package com.thfh.dto;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;

/**
 * 企业相册分类数据传输对象
 */
@Data
@ApiModel(value = "企业相册分类信息", description = "包含企业相册分类的详细信息")
public class CompanyAlbumCategoryDTO {
    @ApiModelProperty(value = "分类ID", notes = "唯一标识", example = "1")
    private Long id;
    
    @ApiModelProperty(value = "分类名称", required = true, example = "办公环境")
    private String name;
    
    @ApiModelProperty(value = "分类描述", example = "公司办公环境相关照片")
    private String description;
    
    @ApiModelProperty(value = "公司ID", example = "100")
    private Long companyId;
    
    @ApiModelProperty(value = "是否启用", example = "true")
    private Boolean enabled;
    
    @ApiModelProperty(value = "创建时间", example = "2023-01-01 10:00:00")
    private LocalDateTime createTime;
    
    @ApiModelProperty(value = "更新时间", example = "2023-01-15 15:30:00")
    private LocalDateTime updateTime;
} 
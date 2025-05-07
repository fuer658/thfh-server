package com.thfh.dto;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 标签数据传输对象
 */
@Data
@ApiModel(value = "标签信息", description = "包含标签的基本信息")
public class TagDTO {
    @ApiModelProperty(value = "标签ID", notes = "唯一标识", example = "1")
    private Long id;
    
    @ApiModelProperty(value = "标签名称", required = true, example = "艺术")
    private String tagName;
}
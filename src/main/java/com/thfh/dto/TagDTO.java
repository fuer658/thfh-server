package com.thfh.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 标签数据传输对象
 */
@Data
@Schema(description = "标签信息 - 包含标签的基本信息")
public class TagDTO {
    @Schema(description = "标签ID", description = "唯一标识", example = "1")
    private Long id;
    
    @Schema(description = "标签名称", required = true, example = "艺术")
    private String tagName;
}
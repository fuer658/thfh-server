package com.thfh.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 课程标签数据传输对象
 */
@Data
@Schema(description = "课程标签信息 - 包含课程标签的基本信息")
public class CourseTagDTO {
    @Schema(description = "标签ID", description = "唯一标识", example = "1")
    private Long id;
    
    @Schema(description = "标签名称", required = true, example = "Java编程")
    private String name;
    
    @Schema(description = "标签描述", example = "Java语言相关课程")
    private String description;
    
    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;
}

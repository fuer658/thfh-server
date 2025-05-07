package com.thfh.dto;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 课程标签数据传输对象
 */
@Data
@ApiModel(value = "课程标签信息", description = "包含课程标签的基本信息")
public class CourseTagDTO {
    @ApiModelProperty(value = "标签ID", notes = "唯一标识", example = "1")
    private Long id;
    
    @ApiModelProperty(value = "标签名称", required = true, example = "Java编程")
    private String name;
    
    @ApiModelProperty(value = "标签描述", example = "Java语言相关课程")
    private String description;
    
    @ApiModelProperty(value = "是否启用", example = "true")
    private Boolean enabled;
}

package com.thfh.dto;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 课程交互数据传输对象
 */
@Data
@ApiModel(value = "课程交互信息", description = "用户对课程的交互状态")
public class CourseInteractionDTO {
    @ApiModelProperty(value = "是否点赞", example = "true")
    private Boolean liked;
    
    @ApiModelProperty(value = "是否收藏", example = "false")
    private Boolean favorited;
}
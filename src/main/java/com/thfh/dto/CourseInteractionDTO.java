package com.thfh.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 课程交互数据传输对象
 */
@Data
@Schema(description = "课程交互信息 - 用户对课程的交互状态")
public class CourseInteractionDTO {
    @Schema(description = "是否点赞", example = "true")
    private Boolean liked;
    
    @Schema(description = "是否收藏", example = "false")
    private Boolean favorited;
}
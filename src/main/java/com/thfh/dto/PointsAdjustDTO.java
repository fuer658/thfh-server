package com.thfh.dto;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 积分调整数据传输对象
 * 用于管理员调整用户积分时传递相关信息
 */
@Data
@ApiModel(value = "积分调整参数", description = "用于管理员调整用户积分")
public class PointsAdjustDTO {
    /**
     * 学生/用户ID
     */
    @ApiModelProperty(value = "用户ID", required = true, example = "100")
    private Long studentId;
    
    /**
     * 调整的积分数量（正数为增加，负数为减少）
     */
    @ApiModelProperty(value = "积分数量", required = true, notes = "正数为增加，负数为减少", example = "100")
    private Integer points;
    
    /**
     * 积分调整说明/原因
     */
    @ApiModelProperty(value = "调整原因", required = true, example = "活动奖励")
    private String description;
    
    /**
     * 是否同时调整经验值
     */
    @ApiModelProperty(value = "是否同时调整经验值", notes = "true表示需要同时调整经验值", example = "true")
    private Boolean includeExperience = false;
    
    /**
     * 调整的经验值数量
     */
    @ApiModelProperty(value = "经验值数量", notes = "当includeExperience为true时有效", example = "200")
    private Integer experienceAmount;
} 
package com.thfh.dto;

import com.thfh.model.PointsType;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 积分记录查询数据传输对象
 * 用于接收前端积分记录列表查询条件
 */
@Data
@Schema(description = "积分记录查询参数 - 用于积分记录列表的筛选条件")
public class PointsQueryDTO {
    /**
     * 学生/用户ID查询条件
     */
    @Schema(description = "用户ID", example = "100")
    private Long studentId;
    
    /**
     * 积分类型查询条件
     */
    @Schema(description = "积分类型", example = "COURSE_PURCHASE")
    private PointsType type;
    
    /**
     * 当前页码，默认为第1页
     */
    @Schema(description = "当前页码", example = "1")
    private Integer pageNum = 1;
    
    /**
     * 每页记录数，默认为10条
     */
    @Schema(description = "每页记录数", example = "10")
    private Integer pageSize = 10;
} 
package com.thfh.dto;

import com.thfh.model.PointsType;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 积分记录数据传输对象
 * 用于在不同层之间传输积分记录信息
 */
@Data
@Schema(description = "积分记录信息 - 包含积分变动的详细记录")
public class PointsRecordDTO {
    /**
     * 积分记录ID，唯一标识
     */
    @Schema(description = "记录ID", description = "唯一标识", example = "1")
    private Long id;
    
    /**
     * 学生/用户ID
     */
    @Schema(description = "用户ID", example = "100")
    private Long studentId;
    
    /**
     * 学生/用户姓名
     */
    @Schema(description = "用户姓名", example = "张三")
    private String studentName;
    
    /**
     * 积分变动数量（正数为增加，负数为减少）
     */
    @Schema(description = "积分变动数量", description = "正数为增加，负数为减少", example = "100")
    private Integer points;
    
    /**
     * 积分变动类型（如：课程购买、任务完成、管理员调整等）
     */
    @Schema(description = "积分变动类型", description = "如课程购买、任务完成等", example = "COURSE_PURCHASE")
    private PointsType type;
    
    /**
     * 积分变动说明/原因
     */
    @Schema(description = "变动说明", example = "购买Java课程")
    private String description;
    
    /**
     * 积分记录创建时间
     */
    @Schema(description = "创建时间", example = "2023-01-01 10:00:00")
    private String createTime;
} 
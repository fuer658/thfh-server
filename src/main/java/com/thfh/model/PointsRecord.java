package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@Entity
@Table(name = "points_record")
@ApiModel(value = "积分记录", description = "用户积分变动记录")
public class PointsRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "记录ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @ApiModelProperty(value = "学员用户", notes = "积分所属的学员")
    private User student;

    @Column(nullable = false)
    @ApiModelProperty(value = "积分变动数量", required = true, example = "100", notes = "正数为增加，负数为减少")
    private Integer points; // 积分变动数量（正数为增加，负数为减少）

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "积分变动类型", required = true, example = "LEARN_COURSE")
    private PointsType type; // 积分变动类型

    @ApiModelProperty(value = "变动说明", example = "完成《汉服设计基础》课程学习")
    private String description; // 变动说明

    @Column(nullable = false)
    @ApiModelProperty(value = "创建时间", example = "2023-01-01T12:00:00")
    private LocalDateTime createTime = LocalDateTime.now();
} 
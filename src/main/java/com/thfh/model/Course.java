package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@Entity
@Table(name = "course")
@ApiModel(description = "课程实体类")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "课程ID", example = "1")
    private Long id;

    @Column(nullable = false)
    @ApiModelProperty(value = "课程标题", required = true)
    private String title; // 课程标题

    @Column(length = 1000)
    @ApiModelProperty(value = "课程描述", notes = "最多1000字符")
    private String description; // 课程描述

    @Column(nullable = false)
    @ApiModelProperty(value = "课程封面图片URL", required = true, example = "http://example.com/cover.jpg")
    private String coverImage; // 课程封面图片

    @ApiModelProperty(value = "课程封面视频URL", example = "http://example.com/cover.mp4")
    private String coverVideo; // 课程封面视频

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    @ApiModelProperty(value = "教员信息")
    private User teacher; // 教员

    @Column(nullable = false)
    @ApiModelProperty(value = "课程价格", required = true, example = "99.99")
    private BigDecimal price; // 课程价格

    @ApiModelProperty(value = "积分价格", example = "1000")
    private Integer pointsPrice; // 积分价格

    @Column(nullable = false)
    @ApiModelProperty(value = "总课时", required = true, example = "16")
    private Integer totalHours; // 总课时

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "课程状态", required = true, example = "PUBLISHED")
    private CourseStatus status; // 课程状态

    @ApiModelProperty(value = "视频URL", example = "http://example.com/video.mp4")
    private String videoUrl; // 视频URL

    @ApiModelProperty(value = "课程资料", notes = "可能包含JSON格式的资料列表")
    private String materials; // 课程资料

    @ApiModelProperty(value = "点赞数", example = "150")
    private Integer likeCount = 0; // 点赞数

    @ApiModelProperty(value = "收藏数", example = "50")
    private Integer favoriteCount = 0; // 收藏数

    @ApiModelProperty(value = "学习人数", example = "200")
    private Integer studentCount = 0; // 学习人数

    @ApiModelProperty(value = "浏览量", example = "100")
    private Integer viewCount = 0; // 浏览量

    @Column(nullable = false)
    @ApiModelProperty(value = "是否启用", example = "true", required = true)
    private Boolean enabled = true;

    @ManyToMany
    @JoinTable(
        name = "course_tag_relation",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ApiModelProperty(value = "课程标签集合")
    private Set<CourseTag> tags = new HashSet<>();

    @Column(nullable = false)
    @ApiModelProperty(value = "创建时间", required = true)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    @ApiModelProperty(value = "更新时间", required = true)
    private LocalDateTime updateTime = LocalDateTime.now();
} 
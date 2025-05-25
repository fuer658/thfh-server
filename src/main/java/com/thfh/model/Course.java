package com.thfh.model;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Entity
@Table(name = "course")
@Schema(description = "课程实体类")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "课程ID", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "课程标题", required = true)
    private String title; // 课程标题

    @Column(length = 1000)
    @Schema(description = "课程描述 - 最多1000字符")
    private String description; // 课程描述

    @Column(nullable = false)
    @Schema(description = "课程封面图片URL", required = true, example = "http://example.com/cover.jpg")
    private String coverImage; // 课程封面图片

    @Schema(description = "课程封面视频URL", example = "http://example.com/cover.mp4")
    private String coverVideo; // 课程封面视频

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    @Schema(description = "教员信息")
    private User teacher; // 教员

    @Column(nullable = false)
    @Schema(description = "课程价格", required = true, example = "99.99")
    private BigDecimal price; // 课程价格

    @Schema(description = "积分价格", example = "1000")
    private Integer pointsPrice; // 积分价格

    @Column(nullable = false)
    @Schema(description = "总课时", required = true, example = "16")
    private Integer totalHours; // 总课时

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "课程状态", required = true, example = "PUBLISHED")
    private CourseStatus status; // 课程状态

    @Schema(description = "视频URL", example = "http://example.com/video.mp4")
    private String videoUrl; // 视频URL

    @Schema(description = "课程资料 - 可能包含JSON格式的资料列表")
    private String materials; // 课程资料

    @Schema(description = "点赞数", example = "150")
    private Integer likeCount = 0; // 点赞数

    @Schema(description = "收藏数", example = "50")
    private Integer favoriteCount = 0; // 收藏数

    @Schema(description = "学习人数", example = "200")
    private Integer studentCount = 0; // 学习人数

    @Schema(description = "浏览量", example = "100")
    private Integer viewCount = 0; // 浏览量

    @Column(nullable = false)
    @Schema(description = "是否启用", example = "true", required = true)
    private Boolean enabled = true;

    @ManyToMany
    @JoinTable(
        name = "course_tag_relation",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Schema(description = "课程标签集合")
    private Set<CourseTag> tags = new HashSet<>();

    @Column(nullable = false)
    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    @Schema(description = "更新时间", required = true)
    private LocalDateTime updateTime = LocalDateTime.now();

    @Column(name = "dev_team", length = 255)
    @Schema(description = "开发团队 - 课程开发团队名称")
    private String devTeam; // 开发团队
} 
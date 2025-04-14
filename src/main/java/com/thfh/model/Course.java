package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 课程标题

    @Column(length = 1000)
    private String description; // 课程描述

    @Column(nullable = false)
    private String coverImage; // 课程封面图片

    private String coverVideo; // 课程封面视频

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher; // 教员

    @Column(nullable = false)
    private BigDecimal price; // 课程价格

    private Integer pointsPrice; // 积分价格

    @Column(nullable = false)
    private Integer totalHours; // 总课时

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CourseStatus status; // 课程状态

    private String videoUrl; // 视频URL

    private String materials; // 课程资料

    private Integer likeCount = 0; // 点赞数

    private Integer favoriteCount = 0; // 收藏数

    private Integer studentCount = 0; // 学习人数

    @Column(nullable = false)
    private Boolean enabled = true;

    @ManyToMany
    @JoinTable(
        name = "course_tag_relation",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<CourseTag> tags = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now();
} 
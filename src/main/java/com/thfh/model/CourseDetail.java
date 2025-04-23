package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程详情实体类
 * 用于存储课程的详细信息，包括课程章节目录、教学大纲等
 */
@Data
@Entity
@Table(name = "course_detail")
public class CourseDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联的课程ID
     */
    @Column(nullable = false, unique = true)
    private Long courseId;

    /**
     * 课程详细介绍
     */
    @Column(length = 2000)
    private String fullDescription;

    /**
     * 学习目标
     */
    @Column(length = 1000)
    private String learningObjectives;

    /**
     * 适合人群
     */
    @Column(length = 500)
    private String targetAudience;

    /**
     * 先修要求
     */
    @Column(length = 500)
    private String prerequisites;

    /**
     * 课程章节（JSON格式存储课程大纲）
     */
    @Column(columnDefinition = "TEXT")
    private String outline;

    /**
     * 课程资料列表（JSON格式存储）
     */
    @Column(columnDefinition = "TEXT")
    private String materialsList;

    /**
     * 是否已发布
     */
    @Column(nullable = false)
    private Boolean published = false;

    /**
     * 创建时间
     */
    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 更新时间
     */
    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now();

    /**
     * 课程章节(一对多关系)
     */
    @OneToMany(mappedBy = "courseDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseChapter> chapters = new ArrayList<>();
    
    /**
     * 设置关联的课程ID
     * @param courseId 课程ID
     */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    /**
     * 获取关联的课程ID
     * @return 课程ID
     */
    public Long getCourseId() {
        return this.courseId;
    }
} 
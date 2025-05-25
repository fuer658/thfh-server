package com.thfh.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 课程详情实体类
 * 用于存储课程的详细信息，包括课程章节目录、教学大纲等
 */
@Data
@Entity
@Table(name = "course_detail")
@Schema(description = "课程详情实体类，用于存储课程的详细信息，包括课程章节目录、教学大纲等")
public class CourseDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "课程详情ID", example = "1")
    private Long id;

    /**
     * 关联的课程ID
     */
    @Column(nullable = false, unique = true)
    @Schema(description = "关联的课程ID", required = true, example = "1")
    private Long courseId;

    /**
     * 课程详细介绍
     */
    @Column(length = 2000)
    @Schema(description = "课程详细介绍 - 最多2000字符")
    private String fullDescription;

    /**
     * 第几次开课
     */
    @Column(nullable = false)
    @Schema(description = "第几次开课", required = true, example = "1")
    private Integer sessionNumber = 1;

    /**
     * 当前课时
     */
    @Column(nullable = false)
    @Schema(description = "当前课时", required = true, example = "0")
    private Integer currentLesson = 0;

    /**
     * 开发团队
     */
    @Column(length = 1000)
    @Schema(description = "开发团队 - 最多1000字符")
    private String developmentTeam;

    /**
     * 课程章节（JSON格式存储课程大纲）
     */
    @Column(columnDefinition = "TEXT")
    @Schema(description = "课程大纲 - JSON格式存储课程大纲")
    private String outline;

    /**
     * 课程资料列表（JSON格式存储）
     */
    @Column(columnDefinition = "TEXT")
    @Schema(description = "课程资料列表 - JSON格式存储")
    private String materialsList;

    /**
     * 是否已发布
     */
    @Column(nullable = false)
    @Schema(description = "是否已发布", required = true, example = "false")
    private Boolean published = false;

    /**
     * 创建时间
     */
    @Column(nullable = false)
    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 更新时间
     */
    @Column(nullable = false)
    @Schema(description = "更新时间", required = true)
    private LocalDateTime updateTime = LocalDateTime.now();

    /**
     * 课程章节(一对多关系)
     */
    @OneToMany(mappedBy = "courseDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Schema(description = "课程章节列表")
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
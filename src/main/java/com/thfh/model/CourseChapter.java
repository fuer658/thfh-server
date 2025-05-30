package com.thfh.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 课程章节实体类
 * 用于表示课程大纲中的章节结构
 */
@Data
@Entity
@Table(name = "course_chapter")
@Schema(description = "课程章节实体类，用于表示课程大纲中的章节结构")
public class CourseChapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "章节ID", example = "1")
    private Long id;

    /**
     * 所属课程详情
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_detail_id", nullable = false)
    @JsonBackReference
    @Schema(description = "所属课程详情")
    private CourseDetail courseDetail;

    /**
     * 章节标题
     */
    @Column(nullable = false)
    @Schema(description = "章节标题", required = true)
    private String title;

    /**
     * 章节描述
     */
    @Column(length = 1000)
    @Schema(description = "章节描述 - 最多1000字符")
    private String description;

    /**
     * 章节顺序
     */
    @Column(nullable = false)
    @Schema(description = "章节顺序", required = true, example = "1")
    private Integer orderIndex;

    /**
     * 章节下的小节列表
     */
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @JsonManagedReference
    @Schema(description = "章节下的小节列表")
    private List<CourseSection> sections = new ArrayList<>();

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
} 
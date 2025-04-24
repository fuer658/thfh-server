package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 课程子小节实体类
 * 用于表示小节下的更细粒度内容
 */
@Data
@Entity
@Table(name = "course_sub_section")
public class CourseSubSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属小节
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    @JsonBackReference
    private CourseSection parentSection;

    /**
     * 子小节标题
     */
    @Column(nullable = false)
    private String title;

    /**
     * 子小节内容
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 子小节类型(视频、文档、练习等)
     */
    @Column(nullable = false)
    private String type;

    /**
     * 媒体资源URL(视频、文档等)
     */
    private String resourceUrl;

    /**
     * 子小节顺序
     */
    @Column(nullable = false)
    private Integer orderIndex;

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
} 
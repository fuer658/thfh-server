package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * 课程小节实体类
 * 用于表示课程章节下的小节内容
 */
@Data
@Entity
@Table(name = "course_section")
public class CourseSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属章节
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    @JsonBackReference
    private CourseChapter chapter;

    /**
     * 小节标题
     */
    @Column(nullable = false)
    private String title;

    /**
     * 小节内容描述
     */
    @Column(length = 1000)
    private String description;

    /**
     * 小节文字内容
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 小节视频URL
     */
    private String videoUrl;

    /**
     * 视频时长(秒)
     */
    private Integer duration;

    /**
     * 小节顺序
     */
    @Column(nullable = false)
    private Integer orderIndex;

    /**
     * 是否免费(用于预览)
     */
    @Column(nullable = false)
    private Boolean isFree = false;

    /**
     * 小节类型（VIDEO, DOCUMENT, PDF, TEXT等）
     */
    @Column(nullable = false)
    private String type = "VIDEO";

    /**
     * 文档URL（如果类型是DOCUMENT）
     */
    private String documentUrl;

    /**
     * PDF URL（如果类型是PDF）
     */
    private String pdfUrl;

    /**
     * 小节下的子小节列表
     */
    @OneToMany(mappedBy = "parentSection", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @JsonManagedReference
    private List<CourseSubSection> subSections = new ArrayList<>();

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
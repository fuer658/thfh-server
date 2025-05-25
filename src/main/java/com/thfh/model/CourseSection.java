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
 * 课程小节实体类
 * 用于表示课程章节下的小节内容
 */
@Data
@Entity
@Table(name = "course_section")
@Schema(description = "课程小节实体类，用于表示课程章节下的小节内容")
public class CourseSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "小节ID", example = "1")
    private Long id;

    /**
     * 所属章节
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    @JsonBackReference
    @Schema(description = "所属章节")
    private CourseChapter chapter;

    /**
     * 小节标题
     */
    @Column(nullable = false)
    @Schema(description = "小节标题", required = true)
    private String title;

    /**
     * 小节内容描述
     */
    @Column(length = 1000)
    @Schema(description = "小节内容描述 - 最多1000字符")
    private String description;

    /**
     * 小节文字内容
     */
    @Column(columnDefinition = "TEXT")
    @Schema(description = "小节文字内容")
    private String content;

    /**
     * 小节视频URL
     */
    @Schema(description = "小节视频URL", example = "http://example.com/video.mp4")
    private String videoUrl;

    /**
     * 视频时长(秒)
     */
    @Schema(description = "视频时长(秒)", example = "600")
    private Integer duration;

    /**
     * 小节顺序
     */
    @Column(nullable = false)
    @Schema(description = "小节顺序", required = true, example = "1")
    private Integer orderIndex;

    /**
     * 是否免费(用于预览)
     */
    @Column(nullable = false)
    @Schema(description = "是否免费(用于预览)", required = true, example = "false")
    private Boolean isFree = false;

    /**
     * 小节类型（VIDEO, DOCUMENT, PDF, TEXT等）
     */
    @Column(nullable = false)
    @Schema(description = "小节类型", required = true, example = "VIDEO", allowableValues = "VIDEO,DOCUMENT,PDF,TEXT")
    private String type = "VIDEO";

    /**
     * 文档URL（如果类型是DOCUMENT）
     */
    @Schema(description = "文档URL（如果类型是DOCUMENT）", example = "http://example.com/document.docx")
    private String documentUrl;

    /**
     * PDF URL（如果类型是PDF）
     */
    @Schema(description = "PDF URL（如果类型是PDF）", example = "http://example.com/document.pdf")
    private String pdfUrl;

    /**
     * 小节下的子小节列表
     */
    @OneToMany(mappedBy = "parentSection", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @JsonManagedReference
    @Schema(description = "小节下的子小节列表")
    private List<CourseSubSection> subSections = new ArrayList<>();

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
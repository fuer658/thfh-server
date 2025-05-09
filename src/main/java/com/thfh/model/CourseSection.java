package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 课程小节实体类
 * 用于表示课程章节下的小节内容
 */
@Data
@Entity
@Table(name = "course_section")
@ApiModel(description = "课程小节实体类，用于表示课程章节下的小节内容")
public class CourseSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "小节ID", example = "1")
    private Long id;

    /**
     * 所属章节
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    @JsonBackReference
    @ApiModelProperty(value = "所属章节")
    private CourseChapter chapter;

    /**
     * 小节标题
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "小节标题", required = true)
    private String title;

    /**
     * 小节内容描述
     */
    @Column(length = 1000)
    @ApiModelProperty(value = "小节内容描述", notes = "最多1000字符")
    private String description;

    /**
     * 小节文字内容
     */
    @Column(columnDefinition = "TEXT")
    @ApiModelProperty(value = "小节文字内容")
    private String content;

    /**
     * 小节视频URL
     */
    @ApiModelProperty(value = "小节视频URL", example = "http://example.com/video.mp4")
    private String videoUrl;

    /**
     * 视频时长(秒)
     */
    @ApiModelProperty(value = "视频时长(秒)", example = "600")
    private Integer duration;

    /**
     * 小节顺序
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "小节顺序", required = true, example = "1")
    private Integer orderIndex;

    /**
     * 是否免费(用于预览)
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "是否免费(用于预览)", required = true, example = "false")
    private Boolean isFree = false;

    /**
     * 小节类型（VIDEO, DOCUMENT, PDF, TEXT等）
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "小节类型", required = true, example = "VIDEO", allowableValues = "VIDEO,DOCUMENT,PDF,TEXT")
    private String type = "VIDEO";

    /**
     * 文档URL（如果类型是DOCUMENT）
     */
    @ApiModelProperty(value = "文档URL（如果类型是DOCUMENT）", example = "http://example.com/document.docx")
    private String documentUrl;

    /**
     * PDF URL（如果类型是PDF）
     */
    @ApiModelProperty(value = "PDF URL（如果类型是PDF）", example = "http://example.com/document.pdf")
    private String pdfUrl;

    /**
     * 小节下的子小节列表
     */
    @OneToMany(mappedBy = "parentSection", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @JsonManagedReference
    @ApiModelProperty(value = "小节下的子小节列表")
    private List<CourseSubSection> subSections = new ArrayList<>();

    /**
     * 创建时间
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "创建时间", required = true)
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 更新时间
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "更新时间", required = true)
    private LocalDateTime updateTime = LocalDateTime.now();
} 
package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 课程子小节实体类
 * 用于表示小节下的更细粒度内容
 */
@Data
@Entity
@Table(name = "course_sub_section")
@ApiModel(value = "课程子小节实体", description = "用于表示小节下的更细粒度内容")
public class CourseSubSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "子小节ID", example = "1")
    private Long id;

    /**
     * 所属小节
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    @JsonBackReference
    @ApiModelProperty(value = "所属小节", notes = "子小节所属的父级小节")
    private CourseSection parentSection;

    /**
     * 子小节标题
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "子小节标题", required = true, example = "基础知识介绍")
    private String title;

    /**
     * 子小节内容
     */
    @Column(columnDefinition = "TEXT")
    @ApiModelProperty(value = "子小节内容", notes = "子小节的详细内容描述", example = "本节将介绍汉服的基本分类...")
    private String content;

    /**
     * 子小节类型(视频、文档、练习等)
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "子小节类型", required = true, example = "VIDEO", notes = "可选值：VIDEO(视频)、DOCUMENT(文档)、EXERCISE(练习)")
    private String type;

    /**
     * 媒体资源URL(视频、文档等)
     */
    @ApiModelProperty(value = "媒体资源URL", example = "https://example.com/video.mp4", notes = "视频、文档等媒体资源的访问地址")
    private String resourceUrl;

    /**
     * 子小节顺序
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "子小节顺序", required = true, example = "1", notes = "同一小节下子小节的显示顺序")
    private Integer orderIndex;

    /**
     * 创建时间
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "创建时间", example = "2023-01-01T12:00:00")
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 更新时间
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "更新时间", example = "2023-01-02T12:00:00")
    private LocalDateTime updateTime = LocalDateTime.now();
} 
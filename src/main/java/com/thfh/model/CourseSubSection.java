package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 课程小节实体
 */
@Data
@Getter
@Setter
@Entity
@Table(name = "course_sub_section")
@Schema(description = "课程小节实体 - 课程章节下的具体小节")
public class CourseSubSection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "小节ID", example = "1")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    @Schema(description = "所属章节")
    private CourseSection section;
    
    @Column(nullable = false)
    @Schema(description = "小节标题", example = "1.1 Java基础入门")
    private String title;
    
    @Schema(description = "小节描述 - 小节的详细介绍", example = "本小节介绍Java的基础语法和概念")
    private String description;
    
    @Schema(description = "视频URL", example = "https://example.com/video.mp4")
    private String videoUrl;
    
    @Schema(description = "视频时长(秒)", example = "600")
    private Integer duration;
    
    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;
    
    @Schema(description = "是否免费", example = "true")
    private Boolean isFree;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @Schema(description = "是否有字幕", example = "true")
    private Boolean hasSubtitle;
    
    @Schema(description = "字幕URL", example = "https://example.com/subtitle.vtt")
    private String subtitleUrl;
    
    @Schema(description = "是否有课件", example = "true")
    private Boolean hasMaterial;
    
    @Schema(description = "课件URL", example = "https://example.com/material.pdf")
    private String materialUrl;
    
    @Schema(description = "是否有习题", example = "true")
    private Boolean hasExercise;
    
    @Schema(description = "习题ID", example = "1")
    private Long exerciseId;
    
    @PrePersist
    public void prePersist() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        if (updateTime == null) {
            updateTime = LocalDateTime.now();
        }
        if (sortOrder == null) {
            sortOrder = 0;
        }
        if (isFree == null) {
            isFree = false;
        }
        if (hasSubtitle == null) {
            hasSubtitle = false;
        }
        if (hasMaterial == null) {
            hasMaterial = false;
        }
        if (hasExercise == null) {
            hasExercise = false;
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}

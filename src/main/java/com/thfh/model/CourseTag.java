package com.thfh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Entity
@Table(name = "course_tags")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Schema(description = "课程标签 - 用于课程分类和筛选的标签")
public class CourseTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "标签ID", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "标签名称", required = true, example = "汉服制作")
    private String name;

    @Column(length = 500)
    @Schema(description = "标签描述", example = "包含汉服制作相关的课程")
    private String description;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled = true;

    // 默认构造函数
    public CourseTag() {}

    // 接收标签名称的构造函数
    public CourseTag(String name) {
        this.name = name;
        this.enabled = true;
    }
}

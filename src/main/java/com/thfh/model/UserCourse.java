package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户课程关联实体
 * 记录用户选课和学习进度信息
 */
@Data
@Entity
@Table(name = "user_course")
@Schema(description = "用户课程关联 - 记录用户选课信息和学习进度")
public class UserCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "关联ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "关联的用户")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @Schema(description = "关联的课程")
    private Course course;

    @Column(nullable = false)
    @Schema(description = "加入时间", example = "2023-01-01T10:00:00")
    private LocalDateTime enrollTime; // 加入时间

    @Schema(description = "最后访问时间", example = "2023-01-02T15:30:00")
    private LocalDateTime lastAccessTime; // 最后访问时间

    @Schema(description = "是否在学习中", example = "true")
    private Boolean isActive = true; // 是否在学习中

    @PrePersist
    public void prePersist() {
        enrollTime = LocalDateTime.now();
        lastAccessTime = LocalDateTime.now();
    }
}
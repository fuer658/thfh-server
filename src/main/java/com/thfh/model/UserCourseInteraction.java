package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户课程互动实体
 * 记录用户对课程的点赞、收藏等互动行为
 */
@Data
@Entity
@Table(name = "user_course_interaction",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"}))
@ApiModel(value = "用户课程互动", description = "记录用户对课程的点赞、收藏等互动行为")
public class UserCourseInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "互动ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ApiModelProperty(value = "互动用户")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @ApiModelProperty(value = "互动课程")
    private Course course;

    @ApiModelProperty(value = "是否点赞", example = "true")
    private Boolean liked = false; // 是否点赞

    @ApiModelProperty(value = "是否收藏", example = "true")
    private Boolean favorited = false; // 是否收藏

    @Column(nullable = false)
    @ApiModelProperty(value = "创建时间", example = "2023-01-01T10:00:00")
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    @ApiModelProperty(value = "更新时间", example = "2023-01-02T15:30:00")
    private LocalDateTime updateTime = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}
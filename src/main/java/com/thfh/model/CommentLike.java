package com.thfh.model;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 评论点赞实体类
 * 用于存储用户对评论的点赞记录
 */
@Data
@Entity
@Table(name = "comment_like", uniqueConstraints = @UniqueConstraint(columnNames = {"comment_id", "user_id"}))
@Schema(description = "评论点赞 - 用户对评论的点赞记录")
public class CommentLike {
    /**
     * 点赞ID，主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "点赞ID", example = "1")
    private Long id;

    /**
     * 关联的评论
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    @Schema(description = "关联的评论", required = true)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private OrderComment comment;

    /**
     * 点赞用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "点赞用户", required = true)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "email", "phone", "createTime", "updateTime", "lastLoginTime", "status", "roles"})
    private User user;

    /**
     * 创建时间
     */
    @Column(nullable = false)
    @Schema(description = "创建时间", example = "2024-01-01 12:00:00", required = true)
    private LocalDateTime createTime = LocalDateTime.now();
} 
package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "post_share", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "post_id"})
})
@Schema(description = "PostShare - 帖子分享记录实体")
public class PostShare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "分享记录ID", example = "1")
    private Long id;

    @Column(name = "user_id", nullable = false)
    @Schema(description = "用户ID", example = "1001", required = true)
    private Long userId;

    @Column(name = "post_id", nullable = false)
    @Schema(description = "帖子ID", example = "2001", required = true)
    private Long postId;

    @Column(name = "create_time", nullable = false)
    @Schema(description = "创建时间", example = "2023-01-01T12:00:00")
    private LocalDateTime createTime = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    @Schema(description = "关联的帖子对象")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @Schema(description = "关联的用户对象")
    private User user;
}
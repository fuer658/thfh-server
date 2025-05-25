package com.thfh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "post_comment_like", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "comment_id"})
})
@Schema(description = "评论点赞 - 用户对评论的点赞记录")
public class PostCommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "点赞ID", example = "1")
    private Long id;

    @Column(name = "user_id", nullable = false)
    @Schema(description = "用户ID", required = true, example = "1")
    private Long userId;

    @Column(name = "comment_id", nullable = false)
    @Schema(description = "评论ID", required = true, example = "1")
    private Long commentId;

    @Column(name = "create_time", nullable = false)
    @Schema(description = "创建时间", example = "2023-01-01T12:00:00")
    private LocalDateTime createTime = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    @Schema(description = "评论对象 - 点赞的评论")
    private PostComment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "email", "phone", "createTime", "updateTime", "lastLoginTime", "status", "roles","disability","points","birthday"})
    @Schema(description = "用户对象 - 点赞的用户")
    private User user;
}

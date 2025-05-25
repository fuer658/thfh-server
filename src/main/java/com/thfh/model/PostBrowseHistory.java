package com.thfh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "post_browse_history", 
       uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "post_id"})})
@Schema(description = "动态浏览记录 - 记录用户浏览动态的历史")
public class PostBrowseHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "记录ID", example = "1")
    private Long id;

    @Column(name = "user_id", nullable = false)
    @Schema(description = "用户ID", required = true, example = "1")
    private Long userId;

    @Column(name = "post_id", nullable = false)
    @Schema(description = "动态ID", required = true, example = "1")
    private Long postId;

    @CreationTimestamp
    @Column(name = "browse_time", nullable = false)
    @Schema(description = "首次浏览时间", example = "2023-05-01T12:00:00")
    private LocalDateTime browseTime;

    @Column(name = "last_browse_time")
    @Schema(description = "最后浏览时间", example = "2023-05-02T14:30:00")
    private LocalDateTime lastBrowseTime;

    @Column(name = "browse_count", nullable = false)
    @Schema(description = "浏览次数", example = "5")
    private Integer browseCount = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "email", "phone", "createTime", "updateTime", "lastLoginTime", "status", "roles", "disability", "points", "birthday"})
    @Schema(description = "用户信息", hidden = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "content", "imageUrls", "tags", "tagIds", "tagNames", "comments"})
    @Schema(description = "动态信息", hidden = true)
    private Post post;
} 
package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "follows")
@Schema(description = "关注关系 - 用户之间的关注关系")
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "关注ID", example = "1")
    private Long id;

    @Column(name = "follower_id", nullable = false)
    @Schema(description = "关注者ID", required = true, example = "1001")
    private Long followerId;

    @Column(name = "followed_id", nullable = false)
    @Schema(description = "被关注者ID", required = true, example = "1002")
    private Long followedId;

    @Column(name = "follow_time", nullable = false)
    @Schema(description = "关注时间", example = "2023-05-20T14:30:00")
    private LocalDateTime followTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", insertable = false, updatable = false)
    @Schema(description = "关注者 - 执行关注操作的用户")
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id", insertable = false, updatable = false)
    @Schema(description = "被关注者 - 被关注的用户")
    private User followed;
}
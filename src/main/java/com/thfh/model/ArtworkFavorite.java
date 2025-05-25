package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "artwork_favorites")
@Schema(description = "作品收藏 - 用户对作品的收藏记录")
public class ArtworkFavorite {
    @Schema(description = "收藏ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "收藏用户")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Schema(description = "收藏的作品")
    @ManyToOne
    @JoinColumn(name = "artwork_id", nullable = false)
    private Artwork artwork;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
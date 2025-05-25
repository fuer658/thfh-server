package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "artwork_like")
@Schema(description = "作品点赞 - 用户对作品的点赞记录")
public class ArtworkLike {

    @Schema(description = "点赞ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "作品信息")
    @ManyToOne
    @JoinColumn(name = "artwork_id", nullable = false)
    private Artwork artwork;

    @Schema(description = "点赞用户")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Schema(description = "创建时间")
    @CreationTimestamp
    private LocalDateTime createTime;
}
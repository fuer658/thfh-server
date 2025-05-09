package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "artwork_favorites")
@ApiModel(value = "作品收藏", description = "用户对作品的收藏记录")
public class ArtworkFavorite {
    @ApiModelProperty(value = "收藏ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "收藏用户")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ApiModelProperty(value = "收藏的作品")
    @ManyToOne
    @JoinColumn(name = "artwork_id", nullable = false)
    private Artwork artwork;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
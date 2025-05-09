package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "artwork_like")
@ApiModel(value = "作品点赞", description = "用户对作品的点赞记录")
public class ArtworkLike {

    @ApiModelProperty(value = "点赞ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "作品信息")
    @ManyToOne
    @JoinColumn(name = "artwork_id", nullable = false)
    private Artwork artwork;

    @ApiModelProperty(value = "点赞用户")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ApiModelProperty(value = "创建时间")
    @CreationTimestamp
    private LocalDateTime createTime;
}
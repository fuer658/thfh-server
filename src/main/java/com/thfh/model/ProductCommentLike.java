package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product_comment_like", uniqueConstraints = {@UniqueConstraint(columnNames = {"comment_id", "user_id"})})
@ApiModel(value = "ProductCommentLike", description = "商品评论点赞记录实体")
public class ProductCommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "点赞记录ID", example = "1")
    private Long id;

    @Column(name = "comment_id", nullable = false)
    @ApiModelProperty(value = "被点赞的评论ID", required = true)
    private Long commentId;

    @Column(name = "user_id", nullable = false)
    @ApiModelProperty(value = "点赞用户ID", required = true)
    private Long userId;

    @Column(nullable = false)
    @ApiModelProperty(value = "点赞时间", example = "2023-01-01T12:00:00", required = true)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 
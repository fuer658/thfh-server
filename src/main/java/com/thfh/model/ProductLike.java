package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product_likes")
@ApiModel(value = "ProductLike", description = "商品点赞记录实体")
public class ProductLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "点赞记录ID", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ApiModelProperty(value = "点赞用户", required = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @ApiModelProperty(value = "被点赞商品", required = true)
    private Product product;

    @Column(nullable = false)
    @ApiModelProperty(value = "点赞时间", example = "2023-01-01T12:00:00", required = true)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 
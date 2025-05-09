package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product_favorites")
@ApiModel(value = "ProductFavorite", description = "商品收藏记录实体")
public class ProductFavorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "收藏记录ID", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ApiModelProperty(value = "收藏用户", required = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @ApiModelProperty(value = "被收藏商品", required = true)
    private Product product;

    @Column(nullable = false)
    @ApiModelProperty(value = "收藏时间", example = "2023-01-01T12:00:00", required = true)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 
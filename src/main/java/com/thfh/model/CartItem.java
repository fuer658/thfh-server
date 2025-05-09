package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cart_item")
@ApiModel(value = "购物车项", description = "购物车中的作品项目")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "购物车项ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "购物车")
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCart cart;

    @ApiModelProperty(value = "作品ID", required = true, example = "1")
    @Column(nullable = false)
    private Long artworkId;

    @ApiModelProperty(value = "作品名称", required = true, example = "春天的色彩")
    @Column(nullable = false)
    private String title;

    @ApiModelProperty(value = "作品封面", example = "http://example.com/images/artwork.jpg")
    private String coverUrl;

    @ApiModelProperty(value = "数量", required = true, example = "1")
    @Column(nullable = false)
    private Integer quantity = 1;

    @ApiModelProperty(value = "价格", required = true, example = "1999.99")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
} 
package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "shopping_cart")
@ApiModel(value = "购物车", description = "用户的购物车信息")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "购物车ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "用户ID", required = true, example = "1")
    @Column(nullable = false)
    private Long userId;

    @ApiModelProperty(value = "总价", example = "1999.99")
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "购物车项目列表")
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

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

    /**
     * 计算购物车总价
     */
    public void calculateTotalPrice() {
        this.totalPrice = items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
} 
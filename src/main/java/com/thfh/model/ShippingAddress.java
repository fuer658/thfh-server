package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 收货地址实体类
 */
@Data
@Entity
@Table(name = "shipping_address")
@ApiModel(value = "收货地址实体", description = "存储用户收货地址信息")
public class ShippingAddress {
    /**
     * 地址ID，主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "地址ID", example = "1")
    private Long id;

    /**
     * 关联的用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ApiModelProperty(value = "关联的用户", required = true)
    private User user;

    /**
     * 收货人姓名
     */
    @Column(nullable = false, length = 50)
    @ApiModelProperty(value = "收货人姓名", example = "张三", required = true)
    private String receiverName;

    /**
     * 收货人电话
     */
    @Column(nullable = false, length = 20)
    @ApiModelProperty(value = "收货人电话", example = "13800138000", required = true)
    private String receiverPhone;

    /**
     * 详细地址
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "详细地址", example = "北京市朝阳区xxx街道xxx号", required = true)
    private String address;

    /**
     * 是否为默认地址
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "是否默认地址", example = "false")
    private Boolean isDefault = false;

    /**
     * 创建时间
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "创建时间", hidden = true)
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 更新时间
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "更新时间", hidden = true)
    private LocalDateTime updateTime = LocalDateTime.now();

    /**
     * 更新实体的updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 
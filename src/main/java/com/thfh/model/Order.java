package com.thfh.model;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 用于存储艺术品订单的相关信息，包括订单基本信息、收货信息和物流信息等
 */
@Data
@Entity
@Table(name = "thfh_order")
@ApiModel(value = "订单实体", description = "包含订单的所有相关信息")
public class Order {
    /**
     * 订单ID，主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "订单ID", example = "1")
    private Long id;

    /**
     * 订单编号，系统生成的唯一标识
     */
    @Column(nullable = false, unique = true, length = 32)
    @ApiModelProperty(value = "订单编号", example = "202401010001", required = true)
    private String orderNo;

    /**
     * 订单关联的用户信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ApiModelProperty(value = "订单所属用户", required = true)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "email", "phone", "createTime", "updateTime", "lastLoginTime", "status", "roles","qualification","speciality","disability","points","birthday"})
    private User user;

    /**
     * 订单关联的艺术品信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id", nullable = false)
    @ApiModelProperty(value = "订单关联的艺术品", required = true)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "creator", "tags"})
    private Artwork artwork;

    /**
     * 订单金额
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "订单金额", example = "1999.99", required = true)
    private BigDecimal amount;

    /**
     * 订单状态（UNPAID-待支付，PAID-已支付，SHIPPED-已发货，COMPLETED-已完成，CANCELLED-已取消）
     */
    @Column(nullable = false, length = 20)
    @ApiModelProperty(value = "订单状态", example = "PAID", required = true, 
        allowableValues = "UNPAID,PAID,SHIPPED,COMPLETED,CANCELLED")
    private String status;

    /**
     * 收货人姓名
     */
    @Column(nullable = false, length = 50)
    @ApiModelProperty(value = "收货人姓名", example = "张三", required = true)
    private String shippingName;

    /**
     * 收货人电话
     */
    @Column(nullable = false, length = 20)
    @ApiModelProperty(value = "收货人电话", example = "13800138000", required = true)
    private String shippingPhone;

    /**
     * 收货地址
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "收货地址", example = "北京市朝阳区xxx街道xxx号", required = true)
    private String shippingAddress;

    /**
     * 物流公司名称
     */
    @Column(length = 50)
    @ApiModelProperty(value = "物流公司", example = "顺丰快递")
    private String logisticsCompany;

    /**
     * 物流单号
     */
    @Column(length = 50)
    @ApiModelProperty(value = "物流单号", example = "SF1234567890")
    private String logisticsNo;

    /**
     * 订单创建时间
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "创建时间", example = "2024-01-01 12:00:00", required = true)
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 订单更新时间
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "更新时间", example = "2024-01-01 12:00:00", required = true)
    private LocalDateTime updateTime = LocalDateTime.now();

    /**
     * 用户名（非数据库字段，用于数据传输）
     */
    @Transient
    @ApiModelProperty(value = "用户名", example = "user123", hidden = true)
    private String username;

    /**
     * 艺术品标题（非数据库字段，用于数据传输）
     */
    @Transient
    @ApiModelProperty(value = "艺术品标题", example = "春天的花", hidden = true)
    private String artworkTitle;
}
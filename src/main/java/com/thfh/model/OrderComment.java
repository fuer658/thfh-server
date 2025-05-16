package com.thfh.model;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 订单评价实体类
 * 用于存储用户对已购买商品的评价信息
 */
@Data
@Entity
@Table(name = "order_comment")
@ApiModel(value = "订单评价", description = "用户对已购买商品的评价信息")
public class OrderComment {
    /**
     * 评价ID，主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "评价ID", example = "1")
    private Long id;

    /**
     * 关联的订单
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @ApiModelProperty(value = "关联的订单", required = true)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Order order;

    /**
     * 评价用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ApiModelProperty(value = "评价用户", required = true)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "email", "phone", "createTime", "updateTime", "lastLoginTime", "status", "roles"})
    private User user;

    /**
     * 评价内容
     */
    @Column(nullable = false, length = 1000)
    @ApiModelProperty(value = "评价内容", example = "商品质量很好，物流很快", required = true)
    private String content;

    /**
     * 评价图片URL，多个图片用逗号分隔
     */
    @Column(length = 1000)
    @ApiModelProperty(value = "评价图片URL，多个图片用逗号分隔", example = "http://example.com/image1.jpg,http://example.com/image2.jpg")
    private String images;

    /**
     * 评价视频URL
     */
    @Column(length = 255)
    @ApiModelProperty(value = "评价视频URL", example = "http://example.com/video.mp4")
    private String video;

    /**
     * 评分（1-10分）
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "评分（1-10分）", example = "9", required = true)
    private Integer score;

    /**
     * 创建时间
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "创建时间", example = "2024-01-01 12:00:00", required = true)
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 更新时间
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "更新时间", example = "2024-01-01 12:00:00", required = true)
    private LocalDateTime updateTime = LocalDateTime.now();

    /**
     * 点赞数量
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "点赞数量", example = "10")
    private Integer likeCount = 0;
} 
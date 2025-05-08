package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品实体类
 * 用于存储商品的基本信息
 */
@Data
@Entity
@Table(name = "products")
@ApiModel(value = "Product", description = "商品实体类，用于存储商品的基本信息")
public class Product {
    /**
     * 商品ID，主键，自动生成
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "商品ID", example = "1")
    private Long id;

    /**
     * 商品名称
     * 不能为空
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "商品名称", required = true, example = "特殊教育教具")
    private String name;

    /**
     * 商品描述
     * 最大长度1000字符
     */
    @Column(length = 1000)
    @ApiModelProperty(value = "商品描述", example = "这是一款专为特殊教育设计的教具，有助于...")
    private String description;

    /**
     * 商品价格
     * 不能为空
     * precision = 10 表示总位数
     * scale = 2 表示小数位数
     */
    @Column(nullable = false, precision = 10, scale = 2)
    @ApiModelProperty(value = "商品价格", required = true, example = "99.99")
    private BigDecimal price;

    /**
     * 商品库存数量
     * 不能为空
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "商品库存数量", required = true, example = "100")
    private Integer stock;

    /**
     * 商品图片URL列表
     * 支持多张图片
     */
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    @ApiModelProperty(value = "商品图片URL列表")
    private List<String> imageUrls;

    /**
     * 商品创建时间
     * 由系统自动生成
     */
    @Column(name = "created_at")
    @ApiModelProperty(value = "商品创建时间", example = "2023-01-01T12:00:00")
    private LocalDateTime createdAt;

    /**
     * 商品最后更新时间
     * 由系统自动更新
     */
    @Column(name = "updated_at")
    @ApiModelProperty(value = "商品最后更新时间", example = "2023-01-10T15:30:00")
    private LocalDateTime updatedAt;

    /**
     * 商品状态
     * DRAFT: 草稿
     * ON_SHELF: 上架
     * OFF_SHELF: 下架
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ApiModelProperty(value = "商品状态", required = true, example = "ON_SHELF", allowableValues = "DRAFT,ON_SHELF,OFF_SHELF")
    private ProductStatus status = ProductStatus.DRAFT;

    /**
     * 搜索关键词
     * 用于商品搜索，包含商品名称、描述等信息的关键词
     */
    @Column(length = 500)
    @ApiModelProperty(value = "搜索关键词", example = "教具,特殊教育,学习工具")
    private String keywords;

    /**
     * 商品分类
     */
    @Column(length = 50)
    @ApiModelProperty(value = "商品分类", example = "教育工具")
    private String category;

    /**
     * 商品点赞数
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "商品点赞数", example = "50")
    private Integer likeCount = 0;

    /**
     * 商品收藏数
     */
    @Column(nullable = false)
    @ApiModelProperty(value = "商品收藏数", example = "30")
    private Integer favoriteCount = 0;

    /**
     * 创建商品时自动设置创建时间和更新时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * 更新商品时自动更新更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 
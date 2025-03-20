package com.thfh.dto;

import com.thfh.model.ProductStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品数据传输对象
 * 用于前后端数据交互
 */
@Data
public class ProductDTO {
    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品库存数量
     */
    private Integer stock;

    /**
     * 商品图片URL列表
     * 支持多张图片
     */
    private List<String> imageUrls;

    /**
     * 商品状态
     */
    private ProductStatus status;

    /**
     * 搜索关键词
     */
    private String keywords;

    /**
     * 商品分类
     */
    private String category;
} 
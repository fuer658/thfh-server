package com.thfh.dto;

import com.thfh.model.ProductStatus;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 商品搜索条件DTO
 */
@Data
public class ProductSearchDTO {
    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 商品分类
     */
    private String category;

    /**
     * 商品状态
     */
    private ProductStatus status;

    /**
     * 最低价格
     */
    private BigDecimal minPrice;

    /**
     * 最高价格
     */
    private BigDecimal maxPrice;

    /**
     * 页码
     */
    private int pageNum = 1;

    /**
     * 每页大小
     */
    private int pageSize = 10;
} 
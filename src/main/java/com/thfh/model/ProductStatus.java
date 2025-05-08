package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 商品状态枚举
 */
@ApiModel(value = "ProductStatus", description = "商品状态枚举")
public enum ProductStatus {
    /**
     * 草稿状态
     */
    @ApiModelProperty(value = "草稿状态", notes = "商品处于编辑阶段，未上架")
    DRAFT,
    
    /**
     * 上架状态
     */
    @ApiModelProperty(value = "上架状态", notes = "商品已上架，可被用户查看和购买")
    ON_SHELF,
    
    /**
     * 下架状态
     */
    @ApiModelProperty(value = "下架状态", notes = "商品已下架，用户无法查看和购买")
    OFF_SHELF
} 
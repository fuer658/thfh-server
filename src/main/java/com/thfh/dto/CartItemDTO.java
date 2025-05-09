package com.thfh.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "购物车项DTO", description = "购物车项数据传输对象")
public class CartItemDTO {
    @ApiModelProperty(value = "购物车项ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "作品ID", required = true, example = "1")
    private Long artworkId;

    @ApiModelProperty(value = "作品名称", example = "春天的色彩")
    private String title;

    @ApiModelProperty(value = "作品封面", example = "http://example.com/images/artwork.jpg")
    private String coverUrl;

    @ApiModelProperty(value = "数量", required = true, example = "1")
    private Integer quantity = 1;

    @ApiModelProperty(value = "价格", example = "1999.99")
    private BigDecimal price;

    @ApiModelProperty(value = "小计", example = "1999.99")
    private BigDecimal subtotal;
} 
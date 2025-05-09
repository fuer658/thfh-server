package com.thfh.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "购物车DTO", description = "购物车数据传输对象")
public class ShoppingCartDTO {
    @ApiModelProperty(value = "购物车ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "用户ID", example = "1")
    private Long userId;

    @ApiModelProperty(value = "总价", example = "1999.99")
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "购物车项目列表")
    private List<CartItemDTO> items = new ArrayList<>();
} 
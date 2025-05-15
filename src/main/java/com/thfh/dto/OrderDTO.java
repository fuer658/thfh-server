package com.thfh.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单数据传输对象
 */
@Data
@ApiModel(value = "订单信息", description = "用于API输出的订单数据")
public class OrderDTO {
    @ApiModelProperty(value = "订单ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "订单编号", example = "202401010001")
    private String orderNo;

    @ApiModelProperty(value = "订单金额", example = "1999.99")
    private BigDecimal amount;

    @ApiModelProperty(value = "订单状态", example = "PAID")
    private String status;

    @ApiModelProperty(value = "收货人姓名", example = "张三")
    private String shoppingName;

    @ApiModelProperty(value = "收货人电话", example = "13800138000")
    private String shoppingPhone;

    @ApiModelProperty(value = "收货地址", example = "北京市朝阳区xxx街道xxx号")
    private String shoppingAddress;

    @ApiModelProperty(value = "物流公司", example = "顺丰快递")
    private String logisticsCompany;

    @ApiModelProperty(value = "物流单号", example = "SF1234567890")
    private String logisticsNo;

    @ApiModelProperty(value = "创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间", example = "2024-01-01 12:00:00")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "艺术品信息")
    private ArtworkDTO artwork;

    @ApiModelProperty(value = "下单用户信息")
    private UserDTO user;
} 
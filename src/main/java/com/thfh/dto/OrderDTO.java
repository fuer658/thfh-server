package com.thfh.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单数据传输对象
 */
@Data
@Schema(description = "订单信息 - 用于API输出的订单数据")
public class OrderDTO {
    @Schema(description = "订单ID", example = "1")
    private Long id;

    @Schema(description = "订单编号", example = "202401010001")
    private String orderNo;

    @Schema(description = "订单金额", example = "1999.99")
    private BigDecimal amount;

    @Schema(description = "订单状态", example = "PAID")
    private String status;

    @Schema(description = "收货人姓名", example = "张三")
    private String shoppingName;

    @Schema(description = "收货人电话", example = "13800138000")
    private String shoppingPhone;

    @Schema(description = "收货地址", example = "北京市朝阳区xxx街道xxx号")
    private String shoppingAddress;

    @Schema(description = "物流公司", example = "顺丰快递")
    private String logisticsCompany;

    @Schema(description = "物流单号", example = "SF1234567890")
    private String logisticsNo;

    @Schema(description = "创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2024-01-01 12:00:00")
    private LocalDateTime updateTime;

    @Schema(description = "艺术品信息")
    private ArtworkDTO artwork;

    @Schema(description = "下单用户信息")
    private UserDTO user;
} 
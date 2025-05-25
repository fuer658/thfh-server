package com.thfh.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 创建订单的数据传输对象
 */
@Data
@Schema(description = "创建订单请求 - 包含创建新订单所需的所有信息")
public class CreateOrderDTO {

    /**
     * 艺术品ID
     */
    @NotNull(message = "艺术品ID不能为空")
    @Schema(description = "艺术品ID", example = "1", required = true)
    private Long artworkId;

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名不能超过50个字符")
    @Schema(description = "收货人姓名", example = "张三", required = true)
    private String shoppingName;

    /**
     * 收货人电话
     */
    @NotBlank(message = "收货人电话不能为空")
    @Size(max = 20, message = "收货人电话不能超过20个字符")
    @Schema(description = "收货人电话", example = "13800138000", required = true)
    private String shoppingPhone;

    /**
     * 收货地址
     */
    @NotBlank(message = "收货地址不能为空")
    @Schema(description = "收货地址", example = "北京市朝阳区xxx街道xxx号", required = true)
    private String shoppingAddress;
} 
package com.thfh.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 创建订单的数据传输对象
 */
@Data
@ApiModel(value = "创建订单请求", description = "包含创建新订单所需的所有信息")
public class CreateOrderDTO {

    /**
     * 艺术品ID
     */
    @NotNull(message = "艺术品ID不能为空")
    @ApiModelProperty(value = "艺术品ID", example = "1", required = true)
    private Long artworkId;

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名不能超过50个字符")
    @ApiModelProperty(value = "收货人姓名", example = "张三", required = true)
    private String shippingName;

    /**
     * 收货人电话
     */
    @NotBlank(message = "收货人电话不能为空")
    @Size(max = 20, message = "收货人电话不能超过20个字符")
    @ApiModelProperty(value = "收货人电话", example = "13800138000", required = true)
    private String shippingPhone;

    /**
     * 收货地址
     */
    @NotBlank(message = "收货地址不能为空")
    @ApiModelProperty(value = "收货地址", example = "北京市朝阳区xxx街道xxx号", required = true)
    private String shippingAddress;
} 
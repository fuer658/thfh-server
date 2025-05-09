package com.thfh.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 收货地址数据传输对象
 */
@Data
@ApiModel(value = "收货地址DTO", description = "用于收货地址的创建和更新")
public class ShippingAddressDTO {
    /**
     * 地址ID，更新时使用
     */
    @ApiModelProperty(value = "地址ID（更新时需要）", example = "1")
    private Long id;

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名不能超过50个字符")
    @ApiModelProperty(value = "收货人姓名", example = "张三", required = true)
    private String receiverName;

    /**
     * 收货人电话
     */
    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号码")
    @ApiModelProperty(value = "收货人电话", example = "13800138000", required = true)
    private String receiverPhone;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    @ApiModelProperty(value = "详细地址", example = "北京市朝阳区xxx街道xxx号", required = true)
    private String address;

    /**
     * 是否为默认地址
     */
    @ApiModelProperty(value = "是否默认地址", example = "false")
    private Boolean isDefault = false;
}
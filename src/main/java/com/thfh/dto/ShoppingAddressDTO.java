package com.thfh.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 收货地址数据传输对象
 */
@Data
@Schema(description = "收货地址DTO - 用于收货地址的创建和更新")
public class ShoppingAddressDTO {
    /**
     * 地址ID，更新时使用
     */
    @Schema(description = "地址ID（更新时需要）", example = "1")
    private Long id;

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名不能超过50个字符")
    @Schema(description = "收货人姓名", example = "张三", required = true)
    private String receiverName;

    /**
     * 收货人电话
     */
    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号码")
    @Schema(description = "收货人电话", example = "13800138000", required = true)
    private String receiverPhone;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    @Schema(description = "详细地址", example = "北京市朝阳区xxx街道xxx号", required = true)
    private String address;

    /**
     * 是否为默认地址
     */
    @Schema(description = "是否默认地址", example = "false")
    private Boolean isDefault = false;
}
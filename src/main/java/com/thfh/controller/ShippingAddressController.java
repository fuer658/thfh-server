package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.ShippingAddressDTO;
import com.thfh.model.ShippingAddress;
import com.thfh.service.ShippingAddressService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 收货地址控制器
 */
@Api(tags = "收货地址管理", description = "用户收货地址相关的API接口")
@RestController
@RequestMapping("/api/shipping-addresses")
@PreAuthorize("hasRole('USER')")
public class ShippingAddressController {

    @Autowired
    private ShippingAddressService shippingAddressService;

    /**
     * 获取当前用户的所有收货地址
     * @return 收货地址列表
     */
    @ApiOperation(value = "获取当前用户的所有收货地址", notes = "返回当前登录用户的所有收货地址信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping
    public Result<List<ShippingAddress>> getAllAddresses() {
        return Result.success(shippingAddressService.getCurrentUserAddresses());
    }

    /**
     * 根据ID获取收货地址
     * @param id 收货地址ID
     * @return 收货地址信息
     */
    @ApiOperation(value = "根据ID获取收货地址", notes = "返回指定ID的收货地址信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "收货地址不存在")
    })
    @GetMapping("/{id}")
    public Result<ShippingAddress> getAddressById(
            @ApiParam(value = "收货地址ID", required = true) @PathVariable Long id) {
        return Result.success(shippingAddressService.getAddressById(id));
    }

    /**
     * 获取默认收货地址
     * @return 默认收货地址信息
     */
    @ApiOperation(value = "获取默认收货地址", notes = "返回当前登录用户的默认收货地址信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/default")
    public Result<ShippingAddress> getDefaultAddress() {
        ShippingAddress defaultAddress = shippingAddressService.getDefaultAddress();
        if (defaultAddress == null) {
            return Result.success(null, "用户没有设置默认收货地址");
        }
        return Result.success(defaultAddress);
    }

    /**
     * 添加收货地址
     * @param addressDTO 收货地址信息
     * @return 添加后的收货地址
     */
    @ApiOperation(value = "添加收货地址", notes = "为当前登录用户添加新的收货地址")
    @ApiResponses({
        @ApiResponse(code = 200, message = "添加成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @PostMapping
    public Result<ShippingAddress> addAddress(
            @ApiParam(value = "收货地址信息", required = true) @Valid @RequestBody ShippingAddressDTO addressDTO) {
        return Result.success(shippingAddressService.addAddress(addressDTO), "添加收货地址成功");
    }

    /**
     * 更新收货地址
     * @param addressDTO 收货地址信息
     * @return 更新后的收货地址
     */
    @ApiOperation(value = "更新收货地址", notes = "更新指定ID的收货地址信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "收货地址不存在")
    })
    @PutMapping
    public Result<ShippingAddress> updateAddress(
            @ApiParam(value = "收货地址信息", required = true) @Valid @RequestBody ShippingAddressDTO addressDTO) {
        return Result.success(shippingAddressService.updateAddress(addressDTO), "更新收货地址成功");
    }

    /**
     * 删除收货地址
     * @param id 收货地址ID
     * @return 操作结果
     */
    @ApiOperation(value = "删除收货地址", notes = "删除指定ID的收货地址")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "收货地址不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteAddress(
            @ApiParam(value = "收货地址ID", required = true) @PathVariable Long id) {
        return Result.success(shippingAddressService.deleteAddress(id), "删除收货地址成功");
    }

    /**
     * 设置默认收货地址
     * @param id 收货地址ID
     * @return 设置为默认的收货地址
     */
    @ApiOperation(value = "设置默认收货地址", notes = "将指定ID的收货地址设置为默认地址")
    @ApiResponses({
        @ApiResponse(code = 200, message = "设置成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "收货地址不存在")
    })
    @PutMapping("/{id}/default")
    public Result<ShippingAddress> setDefaultAddress(
            @ApiParam(value = "收货地址ID", required = true) @PathVariable Long id) {
        return Result.success(shippingAddressService.setDefaultAddress(id), "设置默认收货地址成功");
    }
} 
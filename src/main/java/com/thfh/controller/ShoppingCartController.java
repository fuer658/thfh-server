package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.ShoppingCartDTO;
import com.thfh.model.User;
import com.thfh.service.ShoppingCartService;
import com.thfh.service.UserService;
import com.thfh.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Positive;

/**
 * 购物车控制器
 * 提供购物车相关API接口，包括查看购物车、添加到购物车、更新购物车、移除购物车项和清空购物车等功能
 */
@Api(tags = "购物车管理", description = "购物车相关的API接口，包括查看购物车、添加到购物车、更新购物车、移除购物车项和清空购物车等功能")
@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService shoppingCartService;
    
    @Autowired
    private AdminService adminService;

    /**
     * 获取当前用户的购物车
     * 
     * @param authentication 认证信息
     * @return 购物车信息
     */
    @ApiOperation(value = "获取购物车", notes = "获取当前登录用户的购物车信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping
    public Result<ShoppingCartDTO> getCart(
            @ApiParam(hidden = true) Authentication authentication) {
        User user = userService.getCurrentUser();
        ShoppingCartDTO cart = shoppingCartService.getCartByUserId(user.getId());
        return Result.success(cart);
    }

    /**
     * 添加作品到购物车
     * 
     * @param artworkId 作品ID
     * @param quantity 数量
     * @param authentication 认证信息
     * @return 更新后的购物车
     */
    @ApiOperation(value = "添加到购物车", notes = "将作品添加到购物车中")
    @ApiResponses({
        @ApiResponse(code = 200, message = "添加成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "作品不存在")
    })
    @PostMapping("/add")
    public Result<ShoppingCartDTO> addToCart(
            @ApiParam(value = "作品ID", required = true) @RequestParam Long artworkId,
            @ApiParam(value = "数量", defaultValue = "1") @RequestParam(defaultValue = "1") Integer quantity,
            @ApiParam(hidden = true) Authentication authentication) {
        User user = userService.getCurrentUser();
        ShoppingCartDTO cart = shoppingCartService.addToCart(user.getId(), artworkId, quantity);
        return Result.success(cart);
    }

    /**
     * 更新购物车项数量
     * 
     * @param cartItemId 购物车项ID
     * @param quantity 新数量
     * @param authentication 认证信息
     * @return 更新后的购物车
     */
    @ApiOperation(value = "更新购物车项数量", notes = "更新购物车中的作品数量")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "购物车项不存在")
    })
    @PutMapping("/update")
    public Result<ShoppingCartDTO> updateItemQuantity(
            @ApiParam(value = "购物车项ID", required = true) @RequestParam Long cartItemId,
            @ApiParam(value = "新数量", required = true) @RequestParam Integer quantity,
            @ApiParam(hidden = true) Authentication authentication) {
        User user = userService.getCurrentUser();
        ShoppingCartDTO cart = shoppingCartService.updateItemQuantity(user.getId(), cartItemId, quantity);
        return Result.success(cart);
    }

    /**
     * 移除购物车项
     * 
     * @param cartItemId 购物车项ID
     * @param authentication 认证信息
     * @return 更新后的购物车
     */
    @ApiOperation(value = "移除购物车项", notes = "从购物车中移除指定的作品")
    @ApiResponses({
        @ApiResponse(code = 200, message = "移除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "购物车项不存在")
    })
    @DeleteMapping("/remove")
    public Result<ShoppingCartDTO> removeFromCart(
            @ApiParam(value = "购物车项ID", required = true) @RequestParam Long cartItemId,
            @ApiParam(hidden = true) Authentication authentication) {
        User user = userService.getCurrentUser();
        ShoppingCartDTO cart = shoppingCartService.removeFromCart(user.getId(), cartItemId);
        return Result.success(cart);
    }

    /**
     * 清空购物车
     * 
     * @param authentication 认证信息
     * @return 清空后的购物车
     */
    @ApiOperation(value = "清空购物车", notes = "清空当前用户的购物车")
    @ApiResponses({
        @ApiResponse(code = 200, message = "清空成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @DeleteMapping("/clear")
    public Result<ShoppingCartDTO> clearCart(
            @ApiParam(hidden = true) Authentication authentication) {
        User user = userService.getCurrentUser();
        ShoppingCartDTO cart = shoppingCartService.clearCart(user.getId());
        return Result.success(cart);
    }

    /**
     * 管理员获取指定用户的购物车
     * 
     * @param userId 用户ID
     * @param authentication 认证信息
     * @return 指定用户的购物车信息
     */
    @ApiOperation(value = "管理员获取指定用户的购物车", notes = "管理员根据用户ID获取指定用户的购物车信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有管理员权限"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @GetMapping("/admin/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<ShoppingCartDTO> getCartByUserId(
            @ApiParam(value = "用户ID", required = true) 
            @PathVariable @Positive(message = "用户ID必须为正数") Long userId,
            @ApiParam(hidden = true) Authentication authentication) {
        
        // 验证用户是否存在
        if (!userService.existsById(userId)) {
            return Result.error(HttpStatus.NOT_FOUND.value(), "用户不存在，ID: " + userId);
        }
        
        ShoppingCartDTO cart = shoppingCartService.getCartByUserId(userId);
        return Result.success(cart);
    }
    
    /**
     * 管理员为指定用户添加作品到购物车
     * 
     * @param userId 用户ID
     * @param artworkId 作品ID
     * @param quantity 数量
     * @param authentication 认证信息
     * @return 更新后的购物车
     */
    @ApiOperation(value = "管理员为用户添加作品到购物车", notes = "管理员将作品添加到指定用户的购物车中")
    @ApiResponses({
        @ApiResponse(code = 200, message = "添加成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有管理员权限"),
        @ApiResponse(code = 404, message = "用户或作品不存在")
    })
    @PostMapping("/admin/user/{userId}/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<ShoppingCartDTO> adminAddToCart(
            @ApiParam(value = "用户ID", required = true) 
            @PathVariable @Positive(message = "用户ID必须为正数") Long userId,
            @ApiParam(value = "作品ID", required = true) @RequestParam Long artworkId,
            @ApiParam(value = "数量", defaultValue = "1") @RequestParam(defaultValue = "1") Integer quantity,
            @ApiParam(hidden = true) Authentication authentication) {
        
        // 验证用户是否存在
        if (!userService.existsById(userId)) {
            return Result.error(HttpStatus.NOT_FOUND.value(), "用户不存在，ID: " + userId);
        }
        
        ShoppingCartDTO cart = shoppingCartService.addToCart(userId, artworkId, quantity);
        return Result.success(cart);
    }
} 
package com.thfh.controller;

import com.thfh.dto.CreateOrderDTO;
import com.thfh.dto.LogisticsDTO;
import com.thfh.dto.OrderQueryDTO;
import com.thfh.dto.OrderDTO;
import com.thfh.model.Order;
import com.thfh.service.OrderService;
import com.thfh.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * 订单管理控制器
 * 提供订单的查询、详情查看、状态更新和物流信息管理等功能
 */
@Tag(name = "订单管理", description = "提供订单的查询、详情查看、状态更新和物流信息管理等功能")
@RestController
@RequestMapping("/api/orders")
public class OrderController {


    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     * @param createOrderDTO 创建订单请求参数
     * @return 创建的订单信息
     */
    @Operation(summary = "创建订单", description = "根据艺术品ID创建新的订单")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "艺术品不存在")
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<OrderDTO> createOrder(
            @Valid @RequestBody @Parameter(description = "创建订单请求参数", required = true) CreateOrderDTO createOrderDTO) {
        Order order = orderService.createOrder(createOrderDTO);
        return Result.success(orderService.toOrderDTO(order), "订单创建成功");
    }

    /**
     * 获取订单列表
     * @param queryDTO 查询条件，包含订单号、用户ID、状态和分页信息等
     * @return 订单分页列表
     */
    @Operation(summary = "获取订单列表", description = "根据查询条件获取订单分页列表，支持多种筛选条件")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping
    public Result<Page<OrderDTO>> getOrders(
            @Parameter(description = "查询条件，包含订单号、用户ID、状态和分页信息等") OrderQueryDTO queryDTO) {
        Page<Order> page = orderService.getOrders(queryDTO);
        return Result.success(orderService.toOrderDTOPage(page));
    }

    /**
     * 获取订单详情
     * @param id 订单ID
     * @return 订单详细信息
     */
    @Operation(summary = "获取订单详情", description = "通过订单ID查询订单的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    @GetMapping("/{id}")
    public Result<OrderDTO> getOrderDetail(
            @Parameter(description = "订单ID", required = true) @PathVariable Long id) {
        Order order = orderService.getOrderDetail(id);
        return Result.success(orderService.toOrderDTO(order));
    }

    /**
     * 更新订单状态
     * @param id 订单ID
     * @param body 包含状态信息的请求体
     * @return 操作结果
     */
    @Operation(summary = "更新订单状态", description = "根据订单ID更新订单状态")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限修改该订单状态"),
        @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    @PutMapping("/{id}/status")
    public Result<Void> updateOrderStatus(
        @Parameter(description = "订单ID", required = true) @PathVariable Long id,
        @Parameter(description = "包含状态信息的请求体", required = true) @RequestBody Map<String, String> body
    ) {
        orderService.updateOrderStatus(id, body.get("status"));
        return Result.success(null);
    }

    /**
     * 更新订单物流信息
     * @param id 订单ID
     * @param logisticsDTO 物流信息，包含物流公司和物流单号等
     * @return 操作结果
     */
    @Operation(summary = "更新订单物流信息", description = "根据订单ID更新物流公司和物流单号等信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限修改该订单物流信息"),
        @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    @PutMapping("/{id}/logistics")
    public Result<Void> updateLogistics(
        @Parameter(description = "订单ID", required = true) @PathVariable Long id,
        @Parameter(description = "物流信息，包含物流公司和物流单号等", required = true) @RequestBody LogisticsDTO logisticsDTO
    ) {
        orderService.updateLogistics(id, logisticsDTO);
        return Result.success(null);
    }

    /**
     * 查询物流跟踪信息
     * @param company 物流公司代码
     * @param number 物流单号
     * @return 物流跟踪信息
     */
    @Operation(summary = "查询物流跟踪信息", description = "根据物流公司和物流单号查询物流跟踪信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @GetMapping("/logistics/track")
    public Result<Object> getLogisticsInfo(
        @Parameter(description = "物流公司代码", required = true, example = "SF") @RequestParam String company,
        @Parameter(description = "物流单号", required = true, example = "SF1234567890") @RequestParam String number
    ) {
        Object info = orderService.getLogisticsInfo(company, number);
        return Result.success(info);
    }

    /**
     * 删除订单
     * @param id 订单ID
     * @return 操作结果
     */
    @Operation(summary = "删除订单", description = "根据订单ID删除订单，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限删除该订单"),
        @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> deleteOrder(
        @Parameter(description = "订单ID", required = true) @PathVariable Long id) {
        orderService.deleteOrder(id);
        return Result.success(null, "订单删除成功");
    }

    /**
     * 订单支付（仅状态变更，无实际支付功能）
     * @param id 订单ID
     * @return 操作结果
     */
    @Operation(summary = "订单支付", description = "将订单状态变为已支付，仅普通用户可用，无实际支付功能")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "支付成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限支付该订单"),
        @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    @PostMapping("/{id}/pay")
    public Result<Void> payOrder(
        @Parameter(description = "订单ID", required = true) @PathVariable Long id) {
        orderService.payOrder(id);
        return Result.success(null, "支付成功");
    }

    /**
     * 获取当前登录用户的订单分页
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param status 订单状态（可选）
     * @return 订单DTO分页
     */
    @Operation(summary = "获取当前用户订单", description = "获取当前登录用户的订单分页列表，仅普通用户可用")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Page<OrderDTO>> getMyOrders(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        Page<OrderDTO> page = orderService.getOrdersByCurrentUser(pageNum, pageSize, status);
        return Result.success(page);
    }

    /**
     * 添加商品到购物车
     * @param artworkId 艺术品ID
     * @param createOrderDTO 收货信息
     * @return 购物车订单信息
     */
    @Operation(summary = "添加商品到购物车", description = "将指定艺术品添加到当前用户的购物车")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "添加成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "艺术品不存在")
    })
    @PostMapping("/cart/{artworkId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<OrderDTO> addToCart(
            @Parameter(description = "艺术品ID", required = true) @PathVariable Long artworkId,
            @Parameter(description = "收货信息", required = true) @RequestBody CreateOrderDTO createOrderDTO) {
        Order order = orderService.addToCart(artworkId, createOrderDTO);
        return Result.success(orderService.toOrderDTO(order), "添加购物车成功");
    }

    @GetMapping("/cart/check/{artworkId}")
    public Result<Boolean> checkArtworkInCart(@PathVariable Long artworkId) {
        boolean exists = orderService.checkArtworkInCart(artworkId);
        return Result.success(exists);
    }
} 
package com.thfh.controller;

import com.thfh.dto.CreateOrderDTO;
import com.thfh.dto.LogisticsDTO;
import com.thfh.dto.OrderQueryDTO;
import com.thfh.dto.OrderDTO;
import com.thfh.model.Order;
import com.thfh.service.OrderService;
import com.thfh.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 订单管理控制器
 * 提供订单的查询、详情查看、状态更新和物流信息管理等功能
 */
@Api(tags = "订单管理", description = "提供订单的查询、详情查看、状态更新和物流信息管理等功能")
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
    @ApiOperation(value = "创建订单", notes = "根据艺术品ID创建新的订单")
    @ApiResponses({
        @ApiResponse(code = 200, message = "创建成功"),
        @ApiResponse(code = 400, message = "参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "艺术品不存在")
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<OrderDTO> createOrder(
            @Valid @RequestBody @ApiParam(value = "创建订单请求参数", required = true) CreateOrderDTO createOrderDTO) {
        Order order = orderService.createOrder(createOrderDTO);
        return Result.success(orderService.toOrderDTO(order), "订单创建成功");
    }

    /**
     * 获取订单列表
     * @param queryDTO 查询条件，包含订单号、用户ID、状态和分页信息等
     * @return 订单分页列表
     */
    @ApiOperation(value = "获取订单列表", notes = "根据查询条件获取订单分页列表，支持多种筛选条件")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping
    public Result<Page<OrderDTO>> getOrders(
            @ApiParam(value = "查询条件，包含订单号、用户ID、状态和分页信息等") OrderQueryDTO queryDTO) {
        Page<Order> page = orderService.getOrders(queryDTO);
        return Result.success(orderService.toOrderDTOPage(page));
    }

    /**
     * 获取订单详情
     * @param id 订单ID
     * @return 订单详细信息
     */
    @ApiOperation(value = "获取订单详情", notes = "通过订单ID查询订单的详细信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "订单不存在")
    })
    @GetMapping("/{id}")
    public Result<OrderDTO> getOrderDetail(
            @ApiParam(value = "订单ID", required = true) @PathVariable Long id) {
        Order order = orderService.getOrderDetail(id);
        return Result.success(orderService.toOrderDTO(order));
    }

    /**
     * 更新订单状态
     * @param id 订单ID
     * @param body 包含状态信息的请求体
     * @return 操作结果
     */
    @ApiOperation(value = "更新订单状态", notes = "根据订单ID更新订单状态")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限修改该订单状态"),
        @ApiResponse(code = 404, message = "订单不存在")
    })
    @PutMapping("/{id}/status")
    public Result<Void> updateOrderStatus(
        @ApiParam(value = "订单ID", required = true) @PathVariable Long id,
        @ApiParam(value = "包含状态信息的请求体", required = true) @RequestBody Map<String, String> body
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
    @ApiOperation(value = "更新订单物流信息", notes = "根据订单ID更新物流公司和物流单号等信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限修改该订单物流信息"),
        @ApiResponse(code = 404, message = "订单不存在")
    })
    @PutMapping("/{id}/logistics")
    public Result<Void> updateLogistics(
        @ApiParam(value = "订单ID", required = true) @PathVariable Long id,
        @ApiParam(value = "物流信息，包含物流公司和物流单号等", required = true) @RequestBody LogisticsDTO logisticsDTO
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
    @ApiOperation(value = "查询物流跟踪信息", notes = "根据物流公司和物流单号查询物流跟踪信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "查询成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 400, message = "参数错误")
    })
    @GetMapping("/logistics/track")
    public Result<Object> getLogisticsInfo(
        @ApiParam(value = "物流公司代码", required = true, example = "SF") @RequestParam String company,
        @ApiParam(value = "物流单号", required = true, example = "SF1234567890") @RequestParam String number
    ) {
        Object info = orderService.getLogisticsInfo(company, number);
        return Result.success(info);
    }

    /**
     * 删除订单
     * @param id 订单ID
     * @return 操作结果
     */
    @ApiOperation(value = "删除订单", notes = "根据订单ID删除订单，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限删除该订单"),
        @ApiResponse(code = 404, message = "订单不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> deleteOrder(
        @ApiParam(value = "订单ID", required = true) @PathVariable Long id) {
        orderService.deleteOrder(id);
        return Result.success(null, "订单删除成功");
    }

    /**
     * 订单支付（仅状态变更，无实际支付功能）
     * @param id 订单ID
     * @return 操作结果
     */
    @ApiOperation(value = "订单支付", notes = "将订单状态变为已支付，仅普通用户可用，无实际支付功能")
    @ApiResponses({
        @ApiResponse(code = 200, message = "支付成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限支付该订单"),
        @ApiResponse(code = 404, message = "订单不存在")
    })
    @PostMapping("/{id}/pay")
    public Result<Void> payOrder(
        @ApiParam(value = "订单ID", required = true) @PathVariable Long id) {
        orderService.payOrder(id);
        return Result.success(null, "支付成功");
    }
} 
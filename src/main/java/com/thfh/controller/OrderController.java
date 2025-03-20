package com.thfh.controller;

import com.thfh.dto.LogisticsDTO;
import com.thfh.dto.OrderQueryDTO;
import com.thfh.model.Order;
import com.thfh.service.OrderService;
import com.thfh.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 订单管理控制器
 * 提供订单的查询、详情查看、状态更新和物流信息管理等功能
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取订单列表
     * @param queryDTO 查询条件，包含订单号、用户ID、状态和分页信息等
     * @return 订单分页列表
     */
    @GetMapping
    public Result<Page<Order>> getOrders(OrderQueryDTO queryDTO) {
        Page<Order> page = orderService.getOrders(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取订单详情
     * @param id 订单ID
     * @return 订单详细信息
     */
    @GetMapping("/{id}")
    public Result<Order> getOrderDetail(@PathVariable Long id) {
        Order order = orderService.getOrderDetail(id);
        return Result.success(order);
    }

    /**
     * 更新订单状态
     * @param id 订单ID
     * @param body 包含状态信息的请求体
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateOrderStatus(
        @PathVariable Long id,
        @RequestBody Map<String, String> body
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
    @PutMapping("/{id}/logistics")
    public Result<Void> updateLogistics(
        @PathVariable Long id,
        @RequestBody LogisticsDTO logisticsDTO
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
    @GetMapping("/logistics/track")
    public Result<Object> getLogisticsInfo(
        @RequestParam String company,
        @RequestParam String number
    ) {
        Object info = orderService.getLogisticsInfo(company, number);
        return Result.success(info);
    }
} 
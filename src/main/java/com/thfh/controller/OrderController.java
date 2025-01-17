package com.thfh.controller;

import com.thfh.dto.LogisticsDTO;
import com.thfh.dto.OrderQueryDTO;
import com.thfh.model.Order;
import com.thfh.service.OrderService;
import com.thfh.common.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public R getOrders(OrderQueryDTO queryDTO) {
        Page<Order> page = orderService.getOrders(queryDTO);
        return R.ok().data(page);
    }

    @GetMapping("/{id}")
    public R getOrderDetail(@PathVariable Long id) {
        Order order = orderService.getOrderDetail(id);
        return R.ok().data(order);
    }

    @PutMapping("/{id}/status")
    public R updateOrderStatus(
        @PathVariable Long id,
        @RequestBody Map<String, String> body
    ) {
        orderService.updateOrderStatus(id, body.get("status"));
        return R.ok();
    }

    @PutMapping("/{id}/logistics")
    public R updateLogistics(
        @PathVariable Long id,
        @RequestBody LogisticsDTO logisticsDTO
    ) {
        orderService.updateLogistics(id, logisticsDTO);
        return R.ok();
    }

    @GetMapping("/logistics/track")
    public R getLogisticsInfo(
        @RequestParam String company,
        @RequestParam String number
    ) {
        Object info = orderService.getLogisticsInfo(company, number);
        return R.ok().data(info);
    }
} 
package com.thfh.service;

import com.thfh.dto.LogisticsDTO;
import com.thfh.dto.OrderQueryDTO;
import com.thfh.model.Order;
import org.springframework.data.domain.Page;

public interface OrderService {
    Page<Order> getOrders(OrderQueryDTO queryDTO);
    Order getOrderDetail(Long id);
    void updateOrderStatus(Long id, String status);
    void updateLogistics(Long id, LogisticsDTO logisticsDTO);
    Object getLogisticsInfo(String company, String number);
} 
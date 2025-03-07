package com.thfh.service;

import com.thfh.dto.LogisticsDTO;
import com.thfh.dto.OrderQueryDTO;
import com.thfh.model.Order;
import org.springframework.data.domain.Page;

/**
 * 订单服务接口
 * 定义订单相关的业务逻辑操作，包括订单的查询、状态更新、物流信息管理等功能
 */
public interface OrderService {
    /**
     * 获取订单列表
     * @param queryDTO 查询条件对象，包含订单号、状态、用户ID等过滤条件
     * @return 分页后的订单列表
     */
    Page<Order> getOrders(OrderQueryDTO queryDTO);
    
    /**
     * 获取订单详情
     * @param id 订单ID
     * @return 订单详细信息
     */
    Order getOrderDetail(Long id);
    
    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 新的订单状态
     */
    void updateOrderStatus(Long id, String status);
    
    /**
     * 更新订单物流信息
     * @param id 订单ID
     * @param logisticsDTO 物流信息对象，包含物流公司、物流单号等信息
     */
    void updateLogistics(Long id, LogisticsDTO logisticsDTO);
    
    /**
     * 获取物流跟踪信息
     * @param company 物流公司代码
     * @param number 物流单号
     * @return 物流跟踪信息对象
     */
    Object getLogisticsInfo(String company, String number);
} 
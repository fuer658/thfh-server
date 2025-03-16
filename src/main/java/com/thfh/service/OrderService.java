package com.thfh.service;

import com.thfh.dto.LogisticsDTO;
import com.thfh.dto.OrderQueryDTO;
import com.thfh.model.Order;
import com.thfh.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单服务实现类
 * 定义订单相关的业务逻辑操作，包括订单的查询、状态更新、物流信息管理等功能
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${cainiao.api.url}")
    private String cainiaoApiUrl;

    @Value("${cainiao.api.key}")
    private String cainiaoApiKey;

    /**
     * 获取订单列表
     * @param queryDTO 查询条件对象，包含订单号、状态、用户ID等过滤条件
     * @return 分页后的订单列表
     */
    @Transactional(readOnly = true)
    public Page<Order> getOrders(OrderQueryDTO queryDTO) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageRequest = PageRequest.of(queryDTO.getPageNum() - 1, queryDTO.getPageSize(), sort);

        // 先获取分页数据
        Page<Order> orderPage = orderRepository.findByCondition(
                queryDTO.getOrderNo(),
                queryDTO.getUsername(),
                queryDTO.getStatus(),
                pageRequest
        );

        // 如果有数据，再获取完整的关联数据
        if (!orderPage.isEmpty()) {
            List<Order> ordersWithJoinFetch = orderRepository.findByConditionWithJoinFetch(
                    queryDTO.getOrderNo(),
                    queryDTO.getUsername(),
                    queryDTO.getStatus()
            );

            // 手动分页
            int start = (int) pageRequest.getOffset();
            int end = Math.min((start + pageRequest.getPageSize()), ordersWithJoinFetch.size());

            return new PageImpl<>(
                    ordersWithJoinFetch.subList(start, end),
                    pageRequest,
                    orderPage.getTotalElements()
            );
        }

        return orderPage;
    }
    
    /**
     * 获取订单详情
     * @param id 订单ID
     * @return 订单详细信息
     */
    @Transactional(readOnly = true)
    public Order getOrderDetail(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
    }
    
    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 新的订单状态
     */
    @Transactional
    public void updateOrderStatus(Long id, String status) {
        orderRepository.updateStatus(id, status);
    }
    
    /**
     * 更新订单物流信息
     * @param id 订单ID
     * @param logisticsDTO 物流信息对象，包含物流公司、物流单号等信息
     */
    @Transactional
    public void updateLogistics(Long id, LogisticsDTO logisticsDTO) {
        orderRepository.updateLogistics(id, logisticsDTO.getCompany(), logisticsDTO.getNumber());
    }
    
    /**
     * 获取物流跟踪信息
     * @param company 物流公司代码
     * @param number 物流单号
     * @return 物流跟踪信息对象
     */
    public Object getLogisticsInfo(String company, String number) {
        Map<String, String> params = new HashMap<>();
        params.put("api_key", cainiaoApiKey);
        params.put("company", company);
        params.put("number", number);

        return restTemplate.getForObject(
                cainiaoApiUrl + "?api_key={api_key}&company={company}&number={number}",
                Object.class,
                params
        );
    }
} 
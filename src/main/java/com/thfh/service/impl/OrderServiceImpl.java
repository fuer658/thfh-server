package com.thfh.service.impl;

import com.thfh.dto.LogisticsDTO;
import com.thfh.dto.OrderQueryDTO;
import com.thfh.model.Order;
import com.thfh.repository.OrderRepository;
import com.thfh.service.OrderService;
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

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${cainiao.api.url}")
    private String cainiaoApiUrl;

    @Value("${cainiao.api.key}")
    private String cainiaoApiKey;

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public Order getOrderDetail(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long id, String status) {
        orderRepository.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void updateLogistics(Long id, LogisticsDTO logisticsDTO) {
        orderRepository.updateLogistics(id, logisticsDTO.getCompany(), logisticsDTO.getNumber());
    }

    @Override
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
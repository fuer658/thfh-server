package com.thfh.service;

import com.thfh.dto.CreateOrderDTO;
import com.thfh.dto.LogisticsDTO;
import com.thfh.dto.OrderQueryDTO;
import com.thfh.model.Artwork;
import com.thfh.model.Order;
import com.thfh.model.User;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thfh.dto.OrderDTO;
import com.thfh.dto.ArtworkDTO;
import com.thfh.dto.UserDTO;

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
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ArtworkService artworkService;

    @Value("${cainiao.api.url}")
    private String cainiaoApiUrl;

    @Value("${cainiao.api.key}")
    private String cainiaoApiKey;

    /**
     * 创建订单
     * @param createOrderDTO 创建订单的请求数据
     * @return 创建的订单
     */
    @Transactional
    public Order createOrder(CreateOrderDTO createOrderDTO) {
        // 获取当前用户
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        
        // 获取艺术品信息
        Artwork artwork = artworkService.getArtworkById(createOrderDTO.getArtworkId())
                .orElseThrow(() -> new RuntimeException("艺术品不存在"));
                
        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUser(currentUser);
        order.setArtwork(artwork);
        order.setAmount(artwork.getPrice());
        order.setStatus("UNPAID"); // 初始状态为待支付
        
        // 设置收货信息
        order.setShippingName(createOrderDTO.getShippingName());
        order.setShippingPhone(createOrderDTO.getShippingPhone());
        order.setShippingAddress(createOrderDTO.getShippingAddress());
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        order.setCreateTime(now);
        order.setUpdateTime(now);
        
        // 保存订单
        return orderRepository.save(order);
    }
    
    /**
     * 生成订单编号
     * 格式：年月日+6位随机数
     * @return 订单编号
     */
    private String generateOrderNo() {
        String datePart = LocalDateTime.now().toString().substring(0, 10).replace("-", "");
        String randomPart = String.format("%06d", (int) (Math.random() * 1000000));
        return datePart + randomPart;
    }

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

    /**
     * 实体转DTO
     */
    public OrderDTO toOrderDTO(Order order) {
        if (order == null) return null;
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setAmount(order.getAmount());
        dto.setStatus(order.getStatus());
        dto.setShippingName(order.getShippingName());
        dto.setShippingPhone(order.getShippingPhone());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setLogisticsCompany(order.getLogisticsCompany());
        dto.setLogisticsNo(order.getLogisticsNo());
        dto.setCreateTime(order.getCreateTime());
        dto.setUpdateTime(order.getUpdateTime());
        // ArtworkDTO
        if (order.getArtwork() != null) {
            ArtworkDTO artworkDTO = new ArtworkDTO();
            artworkDTO.setId(order.getArtwork().getId());
            artworkDTO.setTitle(order.getArtwork().getTitle());
            artworkDTO.setCoverUrl(order.getArtwork().getCoverUrl());
            artworkDTO.setPrice(order.getArtwork().getPrice());
            // creator
            if (order.getArtwork().getCreator() != null) {
                artworkDTO.setCreatorId(order.getArtwork().getCreator().getId());
                artworkDTO.setCreatorName(order.getArtwork().getCreator().getUsername());
                artworkDTO.setCreatorAvatar(order.getArtwork().getCreator().getAvatar());
            }
            dto.setArtwork(artworkDTO);
        }
        // UserDTO
        if (order.getUser() != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(order.getUser().getId());
            userDTO.setUsername(order.getUser().getUsername());
            userDTO.setAvatar(order.getUser().getAvatar());
            userDTO.setUserType(order.getUser().getUserType());
            dto.setUser(userDTO);
        }
        return dto;
    }

    /**
     * 分页实体转DTO
     */
    public Page<OrderDTO> toOrderDTOPage(Page<Order> orderPage) {
        return orderPage.map(this::toOrderDTO);
    }

    /**
     * 删除订单
     * @param id 订单ID
     */
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("订单不存在");
        }
        orderRepository.deleteById(id);
    }

    /**
     * 订单支付（仅状态变更，无实际支付功能）
     * @param id 订单ID
     */
    @Transactional
    public void payOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        if (!"UNPAID".equals(order.getStatus())) {
            throw new RuntimeException("订单当前状态不可支付");
        }
        order.setStatus("PAID");
        order.setUpdateTime(LocalDateTime.now());
        orderRepository.save(order);
    }

    /**
     * 获取当前登录用户的订单分页
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param status 订单状态（可选）
     * @return 订单DTO分页
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByCurrentUser(int pageNum, int pageSize, String status) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Order> orderPage;
        if (status != null && !status.isEmpty()) {
            orderPage = orderRepository.findByUserIdAndStatus(currentUser.getId(), status, pageRequest);
        } else {
            orderPage = orderRepository.findByUserId(currentUser.getId(), pageRequest);
        }
        return toOrderDTOPage(orderPage);
    }
} 
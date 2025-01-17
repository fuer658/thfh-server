package com.thfh.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long workId;
    private BigDecimal amount;
    private String status;
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;
    private String logisticsCompany;
    private String logisticsNo;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段
    private String username;
    private String workTitle;
} 
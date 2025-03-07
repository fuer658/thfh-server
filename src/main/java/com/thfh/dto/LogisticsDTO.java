package com.thfh.dto;

import lombok.Data;

/**
 * 物流信息数据传输对象
 * 用于在不同层之间传输订单物流信息
 */
@Data
public class LogisticsDTO {
    /**
     * 物流公司名称
     */
    private String company;
    
    /**
     * 物流单号
     */
    private String number;
} 
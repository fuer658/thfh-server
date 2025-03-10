package com.thfh.dto;

import lombok.Data;

/**
 * 订单查询数据传输对象
 * 用于接收前端订单列表查询条件
 */
@Data
public class OrderQueryDTO {
    /**
     * 订单编号查询条件
     */
    private String orderNo;
    
    /**
     * 用户名查询条件
     */
    private String username;
    
    /**
     * 订单状态查询条件
     */
    private String status;
    
    /**
     * 当前页码，默认为第1页
     */
    private Integer pageNum = 1;
    
    /**
     * 每页记录数，默认为10条
     */
    private Integer pageSize = 10;
} 
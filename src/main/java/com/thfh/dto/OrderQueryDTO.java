package com.thfh.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 订单查询数据传输对象
 * 用于接收前端订单列表查询条件
 */
@Data
@Schema(description = "订单查询参数 - 用于订单列表的筛选条件")
public class OrderQueryDTO {
    /**
     * 订单编号查询条件
     */
    @Schema(description = "订单编号", example = "202301010001")
    private String orderNo;
    
    /**
     * 用户名查询条件
     */
    @Schema(description = "用户名", example = "user123")
    private String username;
    
    /**
     * 订单状态查询条件
     */
    @Schema(description = "订单状态", example = "PAID")
    private String status;
    
    /**
     * 当前页码，默认为第1页
     */
    @Schema(description = "当前页码", example = "1")
    private Integer pageNum = 1;
    
    /**
     * 每页记录数，默认为10条
     */
    @Schema(description = "每页记录数", example = "10")
    private Integer pageSize = 10;
} 
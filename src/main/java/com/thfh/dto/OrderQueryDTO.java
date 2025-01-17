package com.thfh.dto;

import lombok.Data;

@Data
public class OrderQueryDTO {
    private String orderNo;
    private String username;
    private String status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
} 
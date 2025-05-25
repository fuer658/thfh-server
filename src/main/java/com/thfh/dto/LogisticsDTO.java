package com.thfh.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 物流信息数据传输对象
 * 用于在不同层之间传输订单物流信息
 */
@Data
@Schema(description = "物流信息 - 包含订单物流配送信息")
public class LogisticsDTO {
    /**
     * 物流公司名称
     */
    @Schema(description = "物流公司", required = true, example = "顺丰速运")
    private String company;
    
    /**
     * 物流单号
     */
    @Schema(description = "物流单号", required = true, example = "SF1234567890")
    private String number;
} 
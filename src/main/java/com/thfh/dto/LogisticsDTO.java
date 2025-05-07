package com.thfh.dto;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 物流信息数据传输对象
 * 用于在不同层之间传输订单物流信息
 */
@Data
@ApiModel(value = "物流信息", description = "包含订单物流配送信息")
public class LogisticsDTO {
    /**
     * 物流公司名称
     */
    @ApiModelProperty(value = "物流公司", required = true, example = "顺丰速运")
    private String company;
    
    /**
     * 物流单号
     */
    @ApiModelProperty(value = "物流单号", required = true, example = "SF1234567890")
    private String number;
} 
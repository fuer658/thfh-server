package com.thfh.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

/**
 * 订单评价创建数据传输对象
 * 用于接收JSON格式的评论创建请求
 */
@Data
@ApiModel(value = "订单评价创建请求", description = "用于创建订单评价的JSON请求体")
public class OrderCommentCreateDTO {
    
    @ApiModelProperty(value = "评价内容", example = "商品质量很好，物流很快", required = true)
    private String content;
    
    @ApiModelProperty(value = "评分（1-10分）", example = "9", required = true)
    private Integer score;
    
    @ApiModelProperty(value = "评价图片URL列表")
    private List<String> imageUrls;
    
    @ApiModelProperty(value = "评价视频URL", example = "http://example.com/video.mp4")
    private String videoUrl;
} 
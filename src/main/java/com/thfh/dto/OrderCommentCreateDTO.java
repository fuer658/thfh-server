package com.thfh.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * 订单评价创建数据传输对象
 * 用于接收JSON格式的评论创建请求
 */
@Data
@Schema(description = "订单评价创建请求 - 用于创建订单评价的JSON请求体")
public class OrderCommentCreateDTO {
    
    @Schema(description = "评价内容", example = "商品质量很好，物流很快", required = true)
    private String content;
    
    @Schema(description = "评分（1-10分）", example = "9", required = true)
    private Integer score;
    
    @Schema(description = "评价图片URL列表")
    private List<String> imageUrls;
    
    @Schema(description = "评价视频URL", example = "http://example.com/video.mp4")
    private String videoUrl;
} 
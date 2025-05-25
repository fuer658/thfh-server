package com.thfh.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单评价数据传输对象
 */
@Data
@Schema(description = "订单评价信息 - 用于API输出的订单评价数据")
public class OrderCommentDTO {
    @Schema(description = "评价ID", example = "1")
    private Long id;

    @Schema(description = "评价内容", example = "商品质量很好，物流很快")
    private String content;

    @Schema(description = "评价图片URL列表")
    private List<String> images;

    @Schema(description = "评价视频URL", example = "http://example.com/video.mp4")
    private String video;

    @Schema(description = "评分（1-10分）", example = "9")
    private Integer score;

    @Schema(description = "创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2024-01-01 12:00:00")
    private LocalDateTime updateTime;

    @Schema(description = "评价用户信息")
    private UserDTO user;

    @Schema(description = "关联的订单信息")
    private OrderDTO order;

    @Schema(description = "点赞数量", example = "10")
    private Integer likeCount;

    @Schema(description = "当前用户是否已点赞", example = "false")
    private Boolean liked = false;
} 
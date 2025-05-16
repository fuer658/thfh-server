package com.thfh.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单评价数据传输对象
 */
@Data
@ApiModel(value = "订单评价信息", description = "用于API输出的订单评价数据")
public class OrderCommentDTO {
    @ApiModelProperty(value = "评价ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "评价内容", example = "商品质量很好，物流很快")
    private String content;

    @ApiModelProperty(value = "评价图片URL列表")
    private List<String> images;

    @ApiModelProperty(value = "评价视频URL", example = "http://example.com/video.mp4")
    private String video;

    @ApiModelProperty(value = "评分（1-10分）", example = "9")
    private Integer score;

    @ApiModelProperty(value = "创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间", example = "2024-01-01 12:00:00")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "评价用户信息")
    private UserDTO user;

    @ApiModelProperty(value = "关联的订单信息")
    private OrderDTO order;

    @ApiModelProperty(value = "点赞数量", example = "10")
    private Integer likeCount;

    @ApiModelProperty(value = "当前用户是否已点赞", example = "false")
    private Boolean liked = false;
} 
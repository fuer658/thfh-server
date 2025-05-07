package com.thfh.dto;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子数据传输对象
 * 用于在不同层之间传输帖子信息
 */
@Data
@ApiModel(value = "帖子信息", description = "包含帖子的详细信息")
public class PostDTO {
    @ApiModelProperty(value = "帖子ID", notes = "唯一标识", example = "1")
    private Long id;
    
    @ApiModelProperty(value = "帖子标题", required = true, example = "分享我的心得体会")
    private String title;
    
    @ApiModelProperty(value = "帖子内容", required = true, example = "今天我想分享一下...")
    private String content;
    
    @ApiModelProperty(value = "图片URL列表", example = "[\"https://example.com/img1.jpg\", \"https://example.com/img2.jpg\"]")
    private List<String> imageUrls;
    
    @ApiModelProperty(value = "用户ID", example = "100")
    private Long userId;
    
    @ApiModelProperty(value = "用户名", example = "user123")
    private String userName;
    
    @ApiModelProperty(value = "用户真实姓名", example = "张三")
    private String userRealName;
    
    @ApiModelProperty(value = "用户头像", example = "https://example.com/avatar.jpg")
    private String userAvatar;
    
    @ApiModelProperty(value = "点赞数", example = "156")
    private Integer likeCount;
    
    @ApiModelProperty(value = "评论数", example = "32")
    private Integer commentCount;
    
    @ApiModelProperty(value = "分享数", example = "12")
    private Integer shareCount;
    
    @ApiModelProperty(value = "创建时间", example = "2023-01-01 10:00:00")
    private LocalDateTime createTime;
    
    @ApiModelProperty(value = "更新时间", example = "2023-01-01 11:30:00")
    private LocalDateTime updateTime;
} 
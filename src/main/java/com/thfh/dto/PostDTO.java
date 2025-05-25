package com.thfh.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import com.thfh.model.PostTag;

/**
 * 帖子数据传输对象
 * 用于在不同层之间传输帖子信息
 */
@Data
@Schema(description = "帖子信息 - 包含帖子的详细信息")
public class PostDTO {
    @Schema(description = "帖子ID", description = "唯一标识", example = "1")
    private Long id;
    
    @Schema(description = "帖子标题", required = true, example = "分享我的心得体会")
    private String title;
    
    @Schema(description = "帖子内容", required = true, example = "今天我想分享一下...")
    private String content;
    
    @Schema(description = "图片URL列表", example = "[\"https://example.com/img1.jpg\", \"https://example.com/img2.jpg\"]")
    private List<String> imageUrls;
    
    @Schema(description = "用户ID", example = "100")
    private Long userId;
    
    @Schema(description = "用户名", example = "user123")
    private String userName;
    
    @Schema(description = "用户真实姓名", example = "张三")
    private String userRealName;
    
    @Schema(description = "用户头像", example = "https://example.com/avatar.jpg")
    private String userAvatar;
    
    @Schema(description = "点赞数", example = "156")
    private Integer likeCount;
    
    @Schema(description = "评论数", example = "32")
    private Integer commentCount;
    
    @Schema(description = "分享数", example = "12")
    private Integer shareCount;
    
    @Schema(description = "创建时间", example = "2023-01-01 10:00:00")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间", example = "2023-01-01 11:30:00")
    private LocalDateTime updateTime;
    
    @Schema(description = "帖子标签 - 帖子关联的标签集合")
    private Set<PostTag> tags;
} 
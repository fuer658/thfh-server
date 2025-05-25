package com.thfh.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 关注DTO
 */
@Data
@Schema(description = "关注DTO - 用户关注信息")
public class FollowDTO {
    
    @Schema(description = "关注ID - 关注记录的唯一标识", example = "1")
    private Long id;
    
    @Schema(description = "关注者ID - 发起关注的用户ID", example = "1")
    private Long followerId;
    
    @Schema(description = "被关注者ID - 被关注的用户ID", example = "2")
    private Long followedId;
    
    @Schema(description = "关注时间", example = "2023-05-20 14:30:00")
    private String followTime;
    
    @Schema(description = "关注者用户名", example = "zhangsan")
    private String followerUsername;
    
    @Schema(description = "关注者头像", example = "https://example.com/avatar.jpg")
    private String followerAvatar;
    
    @Schema(description = "被关注者用户名", example = "lisi")
    private String followedUsername;
    
    @Schema(description = "被关注者头像", example = "https://example.com/avatar2.jpg")
    private String followedAvatar;
}

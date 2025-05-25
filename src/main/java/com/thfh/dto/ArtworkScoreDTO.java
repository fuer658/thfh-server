package com.thfh.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 作品评分DTO
 */
@Data
@Getter
@Setter
@Schema(description = "作品评分DTO - 用户对作品的评分信息")
public class ArtworkScoreDTO {
    
    @Schema(description = "评分ID - 作品评分的唯一标识", example = "1")
    private Long id;
    
    @Schema(description = "作品ID", example = "1")
    private Long artworkId;
    
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "评分", example = "4.5")
    private Float score;
    
    @Schema(description = "评论", example = "这是一个很好的作品")
    private String comment;
    
    @Schema(description = "评分时间", example = "2023-05-20 14:30:00")
    private String createTime;
    
    @Schema(description = "用户名", example = "zhangsan")
    private String username;
    
    @Schema(description = "用户头像", example = "https://example.com/avatar.jpg")
    private String userAvatar;
}

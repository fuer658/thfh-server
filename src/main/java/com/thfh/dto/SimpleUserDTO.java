package com.thfh.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 简单用户DTO
 */
@Data
@Schema(description = "简单用户DTO - 用于列表展示的简化用户信息")
public class SimpleUserDTO {
    
    @Schema(description = "用户ID - 用户的唯一标识", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "zhangsan")
    private String username;
    
    @Schema(description = "真实姓名", example = "张三")
    private String realName;
    
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    @Schema(description = "用户类型", example = "STUDENT")
    private String userType;
    
    @Schema(description = "是否已关注", example = "false")
    private Boolean isFollowed;
}

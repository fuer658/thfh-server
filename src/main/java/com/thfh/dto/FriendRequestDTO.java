package com.thfh.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Schema(description = "好友请求DTO，带用户名")
@Data
public class FriendRequestDTO {
    @Schema(description = "请求ID")
    private Long id;
    @Schema(description = "发起人用户ID")
    private Long fromUserId;
    @Schema(description = "发起人用户名")
    private String fromUserName;
    @Schema(description = "发起人头像URL")
    private String fromUserAvatar;
    @Schema(description = "发起人个人简介")
    private String fromUserIntroduction;
    @Schema(description = "发起人等级")
    private Integer fromUserLevel;
    @Schema(description = "接收人用户ID")
    private Long toUserId;
    @Schema(description = "接收人用户名")
    private String toUserName;
    @Schema(description = "接收人头像URL")
    private String toUserAvatar;
    @Schema(description = "接收人个人简介")
    private String toUserIntroduction;
    @Schema(description = "接收人等级")
    private Integer toUserLevel;
    @Schema(description = "请求状态 0:待处理 1:同意 2:拒绝 3:撤回")
    private Integer status;
    @Schema(description = "创建时间")
    private Date createdAt;
    @Schema(description = "更新时间")
    private Date updatedAt;
} 
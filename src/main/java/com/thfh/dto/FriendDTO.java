package com.thfh.dto;

import com.thfh.model.UserOnlineStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Schema(description = "好友DTO，带用户名")
@Data
public class FriendDTO {
    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "好友ID")
    private Long friendId;
    @Schema(description = "好友用户名")
    private String friendName;
    @Schema(description = "好友备注")
    private String remark;
    @Schema(description = "成为好友时间")
    private Date createdAt;
    @Schema(description = "好友头像URL")
    private String avatar;
    @Schema(description = "好友个人简介")
    private String introduction;
    @Schema(description = "好友等级")
    private Integer level;
    @Schema(description = "好友在线状态")
    private UserOnlineStatus onlineStatus;
    @Schema(description = "好友最后活跃时间")
    private Date lastActiveTime;
    @Schema(description = "好友真实姓名")
    private String realName;
} 
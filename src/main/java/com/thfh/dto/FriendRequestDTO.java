package com.thfh.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

@ApiModel("好友请求DTO，带用户名")
@Data
public class FriendRequestDTO {
    @ApiModelProperty("请求ID")
    private Long id;
    @ApiModelProperty("发起人用户ID")
    private Long fromUserId;
    @ApiModelProperty("发起人用户名")
    private String fromUserName;
    @ApiModelProperty("发起人头像URL")
    private String fromUserAvatar;
    @ApiModelProperty("发起人个人简介")
    private String fromUserIntroduction;
    @ApiModelProperty("发起人等级")
    private Integer fromUserLevel;
    @ApiModelProperty("接收人用户ID")
    private Long toUserId;
    @ApiModelProperty("接收人用户名")
    private String toUserName;
    @ApiModelProperty("接收人头像URL")
    private String toUserAvatar;
    @ApiModelProperty("接收人个人简介")
    private String toUserIntroduction;
    @ApiModelProperty("接收人等级")
    private Integer toUserLevel;
    @ApiModelProperty("请求状态 0:待处理 1:同意 2:拒绝 3:撤回")
    private Integer status;
    @ApiModelProperty("创建时间")
    private Date createdAt;
    @ApiModelProperty("更新时间")
    private Date updatedAt;
} 
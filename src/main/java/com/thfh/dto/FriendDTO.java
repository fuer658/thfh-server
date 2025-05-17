package com.thfh.dto;

import com.thfh.model.UserOnlineStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

@ApiModel("好友DTO，带用户名")
@Data
public class FriendDTO {
    @ApiModelProperty("主键ID")
    private Long id;
    @ApiModelProperty("用户ID")
    private Long userId;
    @ApiModelProperty("好友ID")
    private Long friendId;
    @ApiModelProperty("好友用户名")
    private String friendName;
    @ApiModelProperty("好友备注")
    private String remark;
    @ApiModelProperty("成为好友时间")
    private Date createdAt;
    @ApiModelProperty("好友头像URL")
    private String avatar;
    @ApiModelProperty("好友个人简介")
    private String introduction;
    @ApiModelProperty("好友等级")
    private Integer level;
    @ApiModelProperty("好友在线状态")
    private UserOnlineStatus onlineStatus;
    @ApiModelProperty("好友最后活跃时间")
    private Date lastActiveTime;
} 
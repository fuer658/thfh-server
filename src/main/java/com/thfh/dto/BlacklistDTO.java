package com.thfh.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

@ApiModel("黑名单DTO")
public class BlacklistDTO {
    @ApiModelProperty("拉黑发起者用户ID")
    private Long userId;

    @ApiModelProperty("被拉黑用户ID")
    private Long blockedId;

    @ApiModelProperty("拉黑时间")
    private LocalDateTime createTime;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBlockedId() { return blockedId; }
    public void setBlockedId(Long blockedId) { this.blockedId = blockedId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
} 
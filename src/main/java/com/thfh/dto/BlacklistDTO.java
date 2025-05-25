package com.thfh.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "黑名单DTO")
public class BlacklistDTO {
    @Schema(description = "拉黑发起者用户ID")
    private Long userId;

    @Schema(description = "被拉黑用户ID")
    private Long blockedId;

    @Schema(description = "拉黑时间")
    private LocalDateTime createTime;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBlockedId() { return blockedId; }
    public void setBlockedId(Long blockedId) { this.blockedId = blockedId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
} 
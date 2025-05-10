package com.thfh.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

@ApiModel(description = "举报作品返回体")
public class ArtworkReportResponse {
    @ApiModelProperty(value = "举报ID")
    private Long id;

    @ApiModelProperty(value = "作品ID")
    private Long artworkId;

    @ApiModelProperty(value = "举报人ID")
    private Long reporterId;

    @ApiModelProperty(value = "举报原因")
    private String reason;

    @ApiModelProperty(value = "举报描述")
    private String description;

    @ApiModelProperty(value = "举报时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "举报状态")
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getArtworkId() { return artworkId; }
    public void setArtworkId(Long artworkId) { this.artworkId = artworkId; }
    public Long getReporterId() { return reporterId; }
    public void setReporterId(Long reporterId) { this.reporterId = reporterId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
} 
package com.thfh.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "举报作品请求体")
public class ArtworkReportRequest {
    @Schema(description = "作品ID", required = true, example = "1")
    @NotNull(message = "作品ID不能为空")
    private Long artworkId;

    @Schema(description = "举报原因", required = true, example = "违规内容/抄袭/其他")
    @NotBlank(message = "举报原因不能为空")
    private String reason;

    @Schema(description = "举报描述", required = false, example = "详细描述举报内容")
    private String description;

    public Long getArtworkId() { return artworkId; }
    public void setArtworkId(Long artworkId) { this.artworkId = artworkId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
} 
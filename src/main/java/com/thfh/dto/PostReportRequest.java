package com.thfh.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

@Schema(description = "举报动态请求体")
public class PostReportRequest {

    @Schema(description = "举报原因", required = true, example = "垃圾广告/违法信息/人身攻击等")
    @NotBlank(message = "举报原因不能为空")
    private String reason;

    @Schema(description = "举报描述", required = false, example = "详细描述举报内容")
    private String description;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
} 
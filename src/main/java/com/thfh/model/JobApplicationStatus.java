package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 职位申请状态枚举
 * 描述职位申请在不同阶段的状态
 */
@Schema(description = "职位申请状态 - 描述职位申请在不同阶段的状态")
public enum JobApplicationStatus {
    @Schema(description = "待处理 - 初始状态，刚提交申请")
    PENDING("待处理"),      // 初始状态，刚提交申请
    
    @Schema(description = "审核中 - 正在审核申请材料")
    REVIEWING("审核中"),    // 正在审核申请材料
    
    @Schema(description = "面试中 - 已安排面试")
    INTERVIEW("面试中"),    // 已安排面试
    
    @Schema(description = "已接受 - 申请已被接受")
    ACCEPTED("已接受"),     // 申请已被接受
    
    @Schema(description = "已拒绝 - 申请已被拒绝")
    REJECTED("已拒绝"),     // 申请已被拒绝
    
    @Schema(description = "已撤回 - 申请人撤回申请")
    WITHDRAWN("已撤回");    // 申请人撤回申请
    
    private final String description;
    
    JobApplicationStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 
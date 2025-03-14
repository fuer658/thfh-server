package com.thfh.model;

/**
 * 职位申请状态枚举
 * 描述职位申请在不同阶段的状态
 */
public enum JobApplicationStatus {
    PENDING("待处理"),      // 初始状态，刚提交申请
    REVIEWING("审核中"),    // 正在审核申请材料
    INTERVIEW("面试中"),    // 已安排面试
    ACCEPTED("已接受"),     // 申请已被接受
    REJECTED("已拒绝"),     // 申请已被拒绝
    WITHDRAWN("已撤回");    // 申请人撤回申请
    
    private final String description;
    
    JobApplicationStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 
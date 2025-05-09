package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 职位申请状态枚举
 * 描述职位申请在不同阶段的状态
 */
@ApiModel(value = "职位申请状态", description = "描述职位申请在不同阶段的状态")
public enum JobApplicationStatus {
    @ApiModelProperty(value = "待处理", notes = "初始状态，刚提交申请")
    PENDING("待处理"),      // 初始状态，刚提交申请
    
    @ApiModelProperty(value = "审核中", notes = "正在审核申请材料")
    REVIEWING("审核中"),    // 正在审核申请材料
    
    @ApiModelProperty(value = "面试中", notes = "已安排面试")
    INTERVIEW("面试中"),    // 已安排面试
    
    @ApiModelProperty(value = "已接受", notes = "申请已被接受")
    ACCEPTED("已接受"),     // 申请已被接受
    
    @ApiModelProperty(value = "已拒绝", notes = "申请已被拒绝")
    REJECTED("已拒绝"),     // 申请已被拒绝
    
    @ApiModelProperty(value = "已撤回", notes = "申请人撤回申请")
    WITHDRAWN("已撤回");    // 申请人撤回申请
    
    private final String description;
    
    JobApplicationStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 
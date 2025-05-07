package com.thfh.dto;

import com.thfh.model.UserType;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户查询数据传输对象
 * 用于接收前端用户列表查询条件
 */
@Data
@ApiModel(value = "用户查询参数", description = "用于接收前端用户列表查询条件")
public class UserQueryDTO {
    /**
     * 用户名查询条件（模糊匹配）
     */
    @ApiModelProperty(value = "用户名查询条件", notes = "支持模糊匹配", example = "user")
    private String username;
    
    /**
     * 真实姓名查询条件（模糊匹配）
     */
    @ApiModelProperty(value = "真实姓名查询条件", notes = "支持模糊匹配", example = "张三")
    private String realName;
    
    /**
     * 用户类型查询条件
     */
    @ApiModelProperty(value = "用户类型查询条件", notes = "STUDENT(学员)或TEACHER(教员)", example = "STUDENT")
    private UserType userType;
    
    /**
     * 账号状态查询条件
     */
    @ApiModelProperty(value = "账号状态查询条件", notes = "true-启用, false-禁用", example = "true")
    private Boolean enabled;
    
    /**
     * 当前页码，默认为第1页
     */
    @ApiModelProperty(value = "当前页码", notes = "默认为第1页", example = "1")
    private Integer pageNum = 1;
    
    /**
     * 每页记录数，默认为10条
     */
    @ApiModelProperty(value = "每页记录数", notes = "默认为10条", example = "10")
    private Integer pageSize = 10;
} 
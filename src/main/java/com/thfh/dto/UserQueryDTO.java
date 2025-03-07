package com.thfh.dto;

import com.thfh.model.UserType;
import lombok.Data;

/**
 * 用户查询数据传输对象
 * 用于接收前端用户列表查询条件
 */
@Data
public class UserQueryDTO {
    /**
     * 用户名查询条件（模糊匹配）
     */
    private String username;
    
    /**
     * 真实姓名查询条件（模糊匹配）
     */
    private String realName;
    
    /**
     * 用户类型查询条件
     */
    private UserType userType;
    
    /**
     * 账号状态查询条件
     */
    private Boolean enabled;
    
    /**
     * 当前页码，默认为第1页
     */
    private Integer pageNum = 1;
    
    /**
     * 每页记录数，默认为10条
     */
    private Integer pageSize = 10;
} 
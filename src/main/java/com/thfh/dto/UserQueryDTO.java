package com.thfh.dto;

import com.thfh.model.UserType;
import lombok.Data;

@Data
public class UserQueryDTO {
    private String username;
    private String realName;
    private UserType userType;
    private Boolean enabled;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
} 
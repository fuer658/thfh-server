package com.thfh.dto;

import com.thfh.model.UserType;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String realName;
    private UserType userType;
    private String phone;
    private String email;
    private String avatar;
    private String introduction;
    private String qualification;
    private String speciality;
    private String disability;
    private Integer points;
    private Boolean enabled;
    private String lastLoginTime;
    private String createTime;
} 
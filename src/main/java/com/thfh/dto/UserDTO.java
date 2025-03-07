package com.thfh.dto;

import com.thfh.model.User;
import com.thfh.model.UserType;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String password;
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
    private String updateTime;
    private String birthday;
    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
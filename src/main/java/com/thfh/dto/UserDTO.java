package com.thfh.dto;

import com.thfh.model.Gender;
import com.thfh.model.User;
import com.thfh.model.UserType;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 用户数据传输对象
 * 用于在不同层之间传输用户信息，避免直接暴露实体类
 */
@Data
public class UserDTO {
    /**
     * 用户ID，唯一标识
     */
    private Long id;
    
    /**
     * 用户名，用于登录
     */
    private String username;
    
    /**
     * 密码，用于登录验证
     */
    private String password;
    
    /**
     * 用户真实姓名
     */
    private String realName;
    
    /**
     * 用户类型，如普通用户、残障人士、企业用户等
     */
    private UserType userType;
    
    /**
     * 用户手机号码
     */
    private String phone;
    
    /**
     * 用户电子邮箱
     */
    private String email;
    
    /**
     * 用户性别
     */
    private Gender gender;
    
    /**
     * 用户头像URL
     */
    private String avatar;
    
    /**
     * 用户个人介绍
     */
    private String introduction;
    
    /**
     * 用户资质认证信息
     */
    private String qualification;
    
    /**
     * 用户专业特长
     */
    private String speciality;
    
    /**
     * 用户残障情况描述
     */
    private String disability;
    
    /**
     * 用户积分
     */
    private Integer points;

    /**
     * 地区
     */
    private  String Locate;
    
    /**
     * 账号是否启用
     */
    private Boolean enabled;
    
    /**
     * 最后登录时间
     */
    private String lastLoginTime;
    
    /**
     * 账号创建时间
     */
    private String createTime;
    
    /**
     * 账号信息更新时间
     */
    private String updateTime;
    
    /**
     * 用户生日
     */
    private String birthday;
    
    /**
     * 将User实体对象转换为UserDTO对象
     * 
     * @param user 用户实体对象
     * @return 转换后的UserDTO对象
     */
    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
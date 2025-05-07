package com.thfh.dto;

import com.thfh.model.Gender;
import com.thfh.model.User;
import com.thfh.model.UserType;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户数据传输对象
 * 用于在不同层之间传输用户信息，避免直接暴露实体类
 */
@Data
@ApiModel(value = "用户信息", description = "包含用户的所有基本信息和扩展信息")
public class UserDTO {
    /**
     * 用户ID，唯一标识
     */
    @ApiModelProperty(value = "用户ID", notes = "自动生成，唯一标识", example = "1")
    private Long id;
    
    /**
     * 用户名，用于登录
     */
    @ApiModelProperty(value = "用户名", required = true, notes = "用于登录系统", example = "user123")
    private String username;
    
    /**
     * 密码，用于登录验证
     */
    @ApiModelProperty(value = "密码", notes = "创建用户时必填，更新用户时可不填", example = "password123")
    private String password;
    
    /**
     * 用户真实姓名
     */
    @ApiModelProperty(value = "真实姓名", example = "张三")
    private String realName;
    
    /**
     * 用户类型，如普通用户、残障人士、企业用户等
     */
    @ApiModelProperty(value = "用户类型", notes = "STUDENT(学员)或TEACHER(教员)", example = "STUDENT")
    private UserType userType;
    
    /**
     * 用户手机号码
     */
    @ApiModelProperty(value = "手机号码", example = "13800138000")
    private String phone;
    
    /**
     * 用户电子邮箱
     */
    @ApiModelProperty(value = "电子邮箱", example = "user@example.com")
    private String email;
    
    /**
     * 用户性别
     */
    @ApiModelProperty(value = "性别", example = "MALE")
    private Gender gender;
    
    /**
     * 用户头像URL
     */
    @ApiModelProperty(value = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    /**
     * 用户个人介绍
     */
    @ApiModelProperty(value = "个人介绍", notes = "限制100字以内", example = "这是我的个人介绍")
    private String introduction;
    
    /**
     * 用户资质认证信息
     */
    @ApiModelProperty(value = "资质认证信息", notes = "教员特有字段", example = "心理咨询师二级证书")
    private String qualification;
    
    /**
     * 用户专业特长
     */
    @ApiModelProperty(value = "专业特长", notes = "教员特有字段", example = "心理咨询、行为矫正")
    private String speciality;
    
    /**
     * 用户残障情况描述
     */
    @ApiModelProperty(value = "残障情况", notes = "学员特有字段", example = "听力障碍")
    private String disability;
    
    /**
     * 用户积分
     */
    @ApiModelProperty(value = "积分", notes = "学员特有字段", example = "100")
    private Integer points;

    /**
     * 地区
     */
    @ApiModelProperty(value = "地区", example = "北京市")
    private  String Locate;
    
    /**
     * 账号是否启用
     */
    @ApiModelProperty(value = "账号是否启用", notes = "true-启用，false-禁用", example = "true")
    private Boolean enabled;
    
    /**
     * 最后登录时间
     */
    @ApiModelProperty(value = "最后登录时间", example = "2023-01-01 12:00:00")
    private String lastLoginTime;
    
    /**
     * 账号创建时间
     */
    @ApiModelProperty(value = "账号创建时间", example = "2023-01-01 10:00:00")
    private String createTime;
    
    /**
     * 账号信息更新时间
     */
    @ApiModelProperty(value = "账号更新时间", example = "2023-01-01 11:00:00")
    private String updateTime;
    
    /**
     * 用户生日
     */
    @ApiModelProperty(value = "用户生日", example = "1990-01-01")
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
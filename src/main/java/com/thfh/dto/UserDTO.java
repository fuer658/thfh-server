package com.thfh.dto;

import com.thfh.model.Gender;
import com.thfh.model.InterestType;
import com.thfh.model.User;
import com.thfh.model.UserType;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.List;

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
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4到20之间")
    private String username;
    
    /**
     * 密码，用于登录验证
     */
    @ApiModelProperty(value = "密码", notes = "创建用户时必须提供密码，更新时不需要", example = "password123")
    @Size(min = 6, max = 20, message = "密码长度必须在6到20之间")
    private String password;
    
    /**
     * 用户真实姓名
     */
    @ApiModelProperty(value = "真实姓名", example = "张三")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String realName;
    
    /**
     * 用户类型，如普通用户、残障人士、企业用户等
     */
    @ApiModelProperty(value = "用户类型", notes = "STUDENT(学员)、TEACHER(教员)或ENTERPRISE(企业人员)", example = "STUDENT")
    private UserType userType;
    
    /**
     * 用户手机号码
     */
    @ApiModelProperty(value = "手机号码", example = "13800138000")
    @Size(max = 20, message = "手机号码长度不能超过20个字符")
    private String phone;
    
    /**
     * 用户电子邮箱
     */
    @ApiModelProperty(value = "电子邮箱", example = "user@example.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
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
    @Size(max = 200, message = "个人简介长度不能超过200个字符")
    private String introduction;
    
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
    private String locate;
    
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
     * 补签卡数量
     */
    @ApiModelProperty(value = "补签卡数量", notes = "学员特有字段")
    private Integer makeupCards;

    /**
     * 经验值
     */
    @ApiModelProperty(value = "经验值")
    private Integer experience;

    /**
     * 用户等级
     */
    @ApiModelProperty(value = "用户等级")
    private Integer level;

    /**
     * 企业ID
     */
    @ApiModelProperty(value = "企业ID", notes = "企业人员特有字段，关联的公司ID")
    private Long companyId;

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称", notes = "企业人员特有字段，关联的公司名称")
    private String companyName;

    /**
     * 公司详细信息
     */
    @ApiModelProperty(value = "公司详细信息", notes = "企业用户注册时用于传递公司详情")
    private CompanyDetails companyDetails;

    /**
     * 用户兴趣列表
     */
    @ApiModelProperty(value = "用户兴趣", notes = "用户的兴趣爱好类型列表")
    private List<InterestType> interests;

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
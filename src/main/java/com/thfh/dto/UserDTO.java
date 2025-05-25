package com.thfh.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户DTO
 */
@Data
@Getter
@Setter
@Schema(description = "用户DTO - 用户数据传输对象")
public class UserDTO {
    
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "zhangsan")
    private String username;
    
    @Schema(description = "真实姓名 - 用户的真实姓名", example = "张三")
    private String realName;
    
    @Schema(description = "手机号码", example = "13800138000")
    private String phone;
    
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;
    
    @Schema(description = "头像URL - 用户头像的URL地址", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    @Schema(description = "用户类型", example = "STUDENT")
    private String userType;
    
    @Schema(description = "账号状态", example = "ACTIVE")
    private String status;
    
    @Schema(description = "创建时间", example = "2023-01-01 12:00:00")
    private String createTime;
    
    @Schema(description = "更新时间", example = "2023-01-01 12:00:00")
    private String updateTime;
    
    @Schema(description = "最后登录时间", example = "2023-01-01 12:00:00")
    private String lastLoginTime;
    
    @Schema(description = "个人简介", example = "热爱学习，积极向上")
    private String bio;
    
    @Schema(description = "性别", example = "MALE")
    private String gender;
    
    @Schema(description = "生日", example = "1990-01-01")
    private String birthday;
    
    @Schema(description = "地址", example = "北京市海淀区")
    private String address;
    
    @Schema(description = "积分", example = "100")
    private Integer points;
    
    @Schema(description = "是否已认证", example = "true")
    private Boolean verified;
    
    @Schema(description = "认证类型", example = "ID")
    private String verificationType;
    
    @Schema(description = "标签 - 用户标签，多个标签以逗号分隔", example = "设计,UI,前端")
    private String tags;
    
    @Schema(description = "关注数量 - 用户关注的其他用户数量", example = "10")
    private Integer followingCount;
    
    @Schema(description = "粉丝数量 - 关注该用户的其他用户数量", example = "20")
    private Integer followersCount;
    
    @Schema(description = "是否是VIP用户", example = "false")
    private Boolean vip;
    
    @Schema(description = "VIP到期时间", example = "2023-12-31 23:59:59")
    private String vipExpireTime;
    
    @Schema(description = "是否已禁言", example = "false")
    private Boolean muted;
    
    @Schema(description = "禁言到期时间", example = "2023-12-31 23:59:59")
    private String muteExpireTime;
    
    @Schema(description = "最后活跃时间", example = "2023-01-01 12:00:00")
    private String lastActiveTime;
    
    @Schema(description = "是否在线", example = "true")
    private Boolean online;
    
    @Schema(description = "设备ID - 用户最后登录的设备ID", example = "device123456")
    private String deviceId;
    
    @Schema(description = "推送ID", example = "push123456")
    private String pushId;
    
    @Schema(description = "推送开关", example = "true")
    private Boolean pushEnabled;
    
    @Schema(description = "隐私设置 - JSON格式的隐私设置", example = "{\"showPhone\":false,\"showEmail\":true}")
    private String privacySettings;
    
    @Schema(description = "企业ID", example = "1")
    private Long companyId;
    
    @Schema(description = "教员ID", example = "2")
    private Long teacherId;
    
    @Schema(description = "残疾类型", example = "视力障碍")
    private String disabilityType;
    
    @Schema(description = "残疾等级", example = "一级")
    private String disabilityLevel;
    
    @Schema(description = "紧急联系人", example = "李四")
    private String emergencyContact;
    
    @Schema(description = "紧急联系人电话", example = "13900139000")
    private String emergencyPhone;
    
    @Schema(description = "备注", example = "这是一个备注")
    private String remark;
}

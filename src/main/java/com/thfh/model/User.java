package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@Entity
@Table(name = "user")
@Schema(description = "用户实体 - 存储用户基本信息，包括学员、教员和企业人员")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    @Schema(description = "用户名", required = true, example = "zhangsan")
    private String username;

    @Column(nullable = false)
    @Schema(description = "密码", required = true, hidden = true)
    private String password;

    @Column(length = 100)
    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Column(length = 11)
    @Schema(description = "手机号码", example = "13800138000")
    private String phone;

    @Column(length = 100)
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "用户类型 - ADMIN:管理员, STUDENT:学员, TEACHER:教员, COMPANY:企业", example = "STUDENT")
    private String userType;

    @Schema(description = "账号状态 - ACTIVE:正常, LOCKED:锁定, DISABLED:禁用", example = "ACTIVE")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "个人简介", example = "热爱学习，积极向上")
    private String bio;

    @Schema(description = "性别 - MALE:男, FEMALE:女, OTHER:其他", example = "MALE")
    private String gender;

    @Schema(description = "生日", example = "1990-01-01")
    private LocalDateTime birthday;

    @Schema(description = "地址", example = "北京市海淀区")
    private String address;

    @Schema(description = "积分", example = "100")
    private Integer points;

    @Schema(description = "是否已认证", example = "true")
    private Boolean verified;

    @Schema(description = "认证类型 - ID:身份证, DISABILITY:残疾证, COMPANY:企业", example = "ID")
    private String verificationType;

    @Schema(description = "标签 - 用户标签，多个标签以逗号分隔", example = "设计,UI,前端")
    private String tags;

    @Schema(description = "关注数量 - 用户关注的其他用户数量", example = "10")
    private Integer followingCount;

    @Schema(description = "粉丝数量 - 关注该用户的其他用户数量", example = "20")
    private Integer followersCount;

    @Schema(description = "经验值", example = "100")
    private Integer experience;

    @Schema(description = "等级", example = "1")
    private Integer level;

    @Schema(description = "个人介绍", example = "这是我的个人介绍")
    private String introduction;

    @Schema(description = "是否是VIP用户", example = "false")
    private Boolean vip;

    @Schema(description = "VIP到期时间")
    private LocalDateTime vipExpireTime;

    @Schema(description = "是否已禁言", example = "false")
    private Boolean muted;

    @Schema(description = "禁言到期时间")
    private LocalDateTime muteExpireTime;

    @Schema(description = "最后活跃时间")
    private LocalDateTime lastActiveTime;

    @Schema(description = "是否在线", example = "true")
    private Boolean online;

    @Schema(description = "设备ID - 用户最后登录的设备ID", example = "device123456")
    private String deviceId;

    @Schema(description = "推送ID - 用于消息推送的设备标识", example = "push123456")
    private String pushId;

    @Schema(description = "推送开关 - 是否接收推送消息", example = "true")
    private Boolean pushEnabled;

    @Schema(description = "隐私设置 - JSON格式的隐私设置", example = "{\"showPhone\":false,\"showEmail\":true}")
    private String privacySettings;

    @Schema(description = "企业ID - 如果用户类型是企业人员，关联的企业ID", example = "1")
    private Long companyId;

    @Schema(description = "企业名称", example = "ABC公司")
    private String companyName;

    @Schema(description = "企业详情")
    @OneToOne
    private CompanyDetails companyDetails;

    @Schema(description = "教员ID - 如果用户是学员，关联的教员ID", example = "2")
    private Long teacherId;

    @Schema(description = "残疾类型 - 如果用户是残疾人，记录残疾类型", example = "视力障碍")
    private String disabilityType;

    @Schema(description = "残疾等级 - 如果用户是残疾人，记录残疾等级", example = "一级")
    private String disabilityLevel;

    @Schema(description = "紧急联系人", example = "李四")
    private String emergencyContact;

    @Schema(description = "紧急联系人电话", example = "13900139000")
    private String emergencyPhone;

    @Schema(description = "备注", example = "这是一个备注")
    private String remark;

    @PrePersist
    public void prePersist() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        if (updateTime == null) {
            updateTime = LocalDateTime.now();
        }
        if (status == null) {
            status = "ACTIVE";
        }
        if (points == null) {
            points = 0;
        }
        if (verified == null) {
            verified = false;
        }
        if (vip == null) {
            vip = false;
        }
        if (muted == null) {
            muted = false;
        }
        if (online == null) {
            online = false;
        }
        if (pushEnabled == null) {
            pushEnabled = true;
        }
        if (followingCount == null) {
            followingCount = 0;
        }
        if (followersCount == null) {
            followersCount = 0;
        }
        if (experience == null) {
            experience = 0;
        }
        if (level == null) {
            level = 1;
        }
        if (introduction == null) {
            introduction = "";
        }
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}

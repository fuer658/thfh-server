package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "user")
@ApiModel(value = "用户实体", description = "存储用户基本信息，包括学员、教员和企业人员")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "用户ID", example = "1")
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    @ApiModelProperty(value = "用户名", required = true, example = "zhangsan")
    private String username;

    @Column(nullable = false)
    @ApiModelProperty(value = "密码", required = true, hidden = true)
    private String password;

    @Column(length = 100)
    @ApiModelProperty(value = "真实姓名", example = "张三")
    private String realName;

    @Column(length = 100, name = "locate")
    @ApiModelProperty(value = "用户所在地区", example = "北京市海淀区")
    private String locate; // 用户所在地区

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "用户类型", required = true, example = "STUDENT", notes = "STUDENT(学员)、TEACHER(教员)或ENTERPRISE(企业人员)")
    private UserType userType; // STUDENT(学员)、TEACHER(教员)或ENTERPRISE(企业人员)

    @Column(length = 50)
    @ApiModelProperty(value = "电话号码", example = "13800138000")
    private String phone;

    @Column(length = 100)
    @ApiModelProperty(value = "电子邮箱", example = "zhangsan@example.com")
    private String email;

    @Column(nullable = false)
    @Convert(converter = GenderConverter.class)
    @ApiModelProperty(value = "性别", example = "MALE", notes = "默认为UNKNOWN")
    private Gender gender = Gender.UNKNOWN; // 性别，默认为未知

    @ApiModelProperty(value = "头像URL", example = "http://example.com/avatar.jpg")
    private String avatar;

    @Column(length = 100)
    @ApiModelProperty(value = "个人介绍", example = "我是一名热爱学习的用户")
    private String introduction;

    // 教员特有字段
    @ApiModelProperty(value = "资质证书", example = "高级教师资格证", notes = "教员特有字段")
    private String qualification; // 资质证书
    
    @ApiModelProperty(value = "专长领域", example = "Java编程,数据结构", notes = "教员特有字段")
    private String speciality; // 专长领域

    // 学员特有字段
    @ApiModelProperty(value = "残疾类型", example = "视力障碍", notes = "学员特有字段")
    private String disability; // 残疾类型
    
    @ApiModelProperty(value = "积分", example = "100", notes = "学员特有字段")
    private Integer points = 0; // 积分
    
    @ApiModelProperty(value = "补签卡数量", example = "5", notes = "学员特有字段")
    private Integer makeupCards = 0; // 补签卡数量

    // 企业人员特有字段
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @ApiModelProperty(value = "关联的公司", notes = "企业人员特有字段")
    private Company company; // 关联的公司

    @Column(nullable = false)
    @ApiModelProperty(value = "经验值", example = "500")
    private Integer experience = 0; // 经验值

    @Column(nullable = false)
    @ApiModelProperty(value = "用户等级", example = "3")
    private Integer level = 1; // 用户等级

    @Column(nullable = false)
    @ApiModelProperty(value = "是否启用", example = "true")
    private Boolean enabled = true;

    @ApiModelProperty(value = "最后登录时间", example = "2023-01-01T12:00:00")
    private LocalDateTime lastLoginTime;

    @Column(nullable = false)
    @ApiModelProperty(value = "创建时间", example = "2023-01-01T10:00:00")
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    @ApiModelProperty(value = "更新时间", example = "2023-01-01T11:00:00")
    private LocalDateTime updateTime = LocalDateTime.now();

    @Column
    @ApiModelProperty(value = "生日", example = "2000-01-01T00:00:00")
    private LocalDateTime birthday = LocalDateTime.now();
    
    // 添加与JobApplication的一对多关联
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ApiModelProperty(value = "用户的工作申请列表")
    private List<JobApplication> jobApplications = new ArrayList<>();
    
    // 添加与UserInterest的一对多关联
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ApiModelProperty(value = "用户的兴趣爱好列表")
    private List<UserInterest> userInterests = new ArrayList<>();
    
    @Column(length = 45)
    @ApiModelProperty(value = "最近登录IP", example = "192.168.1.1")
    private String recentLoginIp;
    
    /**
     * 更新实体的updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}

/**
 * 性别转换器
 */
@Converter
class GenderConverter implements AttributeConverter<Gender, String> {
    @Override
    public String convertToDatabaseColumn(Gender gender) {
        return gender == null ? null : gender.name();
    }

    @Override
    public Gender convertToEntityAttribute(String value) {
        return Gender.fromString(value);
    }
}
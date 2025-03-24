package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String realName;

    @Column(length = 100, name = "locate")
    private String locate; // 用户所在地区

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType; // STUDENT(学员) 或 TEACHER(教员)

    @Column(length = 50)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(nullable = false)
    @Convert(converter = GenderConverter.class)
    private Gender gender = Gender.UNKNOWN; // 性别，默认为未知

    private String avatar;

    @Column(length = 100)
    private String introduction;

    // 教员特有字段
    private String qualification; // 资质证书
    private String speciality; // 专长领域

    // 学员特有字段
    private String disability; // 残疾类型
    private Integer points = 0; // 积分
    private Integer makeupCards = 0; // 补签卡数量

    @Column(nullable = false)
    private Integer experience = 0; // 经验值

    @Column(nullable = false)
    private Integer level = 1; // 用户等级

    @Column(nullable = false)
    private Boolean enabled = true;

    private LocalDateTime lastLoginTime;

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now();

    @Column
    private LocalDateTime birthday = LocalDateTime.now();
    
    // 添加与JobApplication的一对多关联
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> jobApplications = new ArrayList<>();
    
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
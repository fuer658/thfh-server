package com.thfh.model;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户兴趣关联实体
 * 用于存储用户与兴趣类型之间的多对多关系
 */
@Data
@Entity
@Table(name = "user_interest")
@ApiModel(value = "用户兴趣关联", description = "用户与兴趣类型之间的多对多关系")
public class UserInterest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "ID", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ApiModelProperty(value = "关联的用户")
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "兴趣偏好", example = "PATTERN_DESIGN")
    private InterestType interestType;

    @Column(nullable = false)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime = LocalDateTime.now();

    /**
     * 更新实体的updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 
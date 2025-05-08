package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户关注关系实体
 * 记录用户之间的关注关系
 */
@Data
@Entity
@Table(name = "user_follow")
@ApiModel(value = "用户关注关系", description = "记录用户之间的关注关系")
public class UserFollow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "关注关系ID", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    @ApiModelProperty(value = "关注者", notes = "执行关注操作的用户")
    private User follower; // 关注者

    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    @ApiModelProperty(value = "被关注者", notes = "被关注的用户")
    private User following; // 被关注者

    @Column(nullable = false)
    @ApiModelProperty(value = "创建时间", example = "2023-01-01T10:00:00")
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    @ApiModelProperty(value = "更新时间", example = "2023-01-02T15:30:00")
    private LocalDateTime updateTime = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 
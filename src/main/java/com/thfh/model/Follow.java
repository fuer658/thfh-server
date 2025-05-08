package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "follows")
@ApiModel(value = "关注关系", description = "用户之间的关注关系")
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "关注ID", example = "1", position = 1)
    private Long id;

    @Column(name = "follower_id", nullable = false)
    @ApiModelProperty(value = "关注者ID", required = true, example = "1001", position = 2)
    private Long followerId;

    @Column(name = "followed_id", nullable = false)
    @ApiModelProperty(value = "被关注者ID", required = true, example = "1002", position = 3)
    private Long followedId;

    @Column(name = "follow_time", nullable = false)
    @ApiModelProperty(value = "关注时间", example = "2023-05-20T14:30:00", position = 4)
    private LocalDateTime followTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", insertable = false, updatable = false)
    @ApiModelProperty(value = "关注者", notes = "执行关注操作的用户", position = 5)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id", insertable = false, updatable = false)
    @ApiModelProperty(value = "被关注者", notes = "被关注的用户", position = 6)
    private User followed;
}
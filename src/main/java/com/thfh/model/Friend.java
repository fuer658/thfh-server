package com.thfh.model;

import javax.persistence.*;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("好友关系实体")
@Entity
@Table(name = "friend")
public class Friend {
    @ApiModelProperty("主键ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty("用户ID")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ApiModelProperty("好友ID")
    @Column(name = "friend_id", nullable = false)
    private Long friendId;

    @ApiModelProperty("成为好友时间")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt = new Date();

    // getter/setter 省略，可用 Lombok 简化

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
} 
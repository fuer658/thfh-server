package com.thfh.model;

import jakarta.persistence.*;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "好友请求实体")
@Entity
@Table(name = "friend_request")
public class FriendRequest {
    @Schema(description = "主键ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "发起人用户ID")
    @Column(name = "from_user_id", nullable = false)
    private Long fromUserId;

    @Schema(description = "接收人用户ID")
    @Column(name = "to_user_id", nullable = false)
    private Long toUserId;

    /**
     * 0: 待处理 1: 同意 2: 拒绝 3: 撤回
     */
    @Schema(description = "请求状态 0:待处理 1:同意 2:拒绝 3:撤回")
    @Column(nullable = false)
    private Integer status = 0;

    @Schema(description = "创建时间")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt = new Date();

    @Schema(description = "更新时间")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt = new Date();

    // getter/setter 省略，可用 Lombok 简化

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
} 
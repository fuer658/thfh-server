package com.thfh.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blacklist", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "blocked_id"})})
public class Blacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "blocked_id", nullable = false)
    private Long blockedId;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    // getter/setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBlockedId() { return blockedId; }
    public void setBlockedId(Long blockedId) { this.blockedId = blockedId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
} 
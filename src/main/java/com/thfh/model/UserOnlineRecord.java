package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户在线状态记录实体
 * 记录用户的在线状态信息
 */
@Data
@Entity
@Table(name = "user_online_record")
@Schema(description = "用户在线状态记录 - 记录用户的在线状态变更信息")
public class UserOnlineRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "记录ID", example = "1")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "用户")
    private User user;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "在线状态", example = "ONLINE")
    private UserOnlineStatus status;
    
    @Column(name = "last_active", nullable = false)
    @Schema(description = "最后活跃时间")
    private LocalDateTime lastActive;
    
    @Column(nullable = false)
    @Schema(description = "创建时间")
    private LocalDateTime createTime = LocalDateTime.now();
    
    @Column(nullable = false)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime = LocalDateTime.now();
    
    /**
     * 更新实体的updateTime
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 
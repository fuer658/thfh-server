package com.thfh.model;

import jakarta.persistence.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "check_in")
@Schema(description = "签到实体类")
public class CheckIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "签到ID", example = "1")
    private Long id;

    @Column(name = "user_id", nullable = false)
    @Schema(description = "用户ID", example = "1", required = true)
    private Long userId;

    @Column(name = "check_in_time", nullable = false)
    @Schema(description = "签到时间", required = true)
    private LocalDateTime checkInTime;

    @Column(name = "is_makeup", nullable = false)
    @Schema(description = "是否为补签", example = "false", required = true)
    private Boolean isMakeup = false;

    @Column(name = "created_at", nullable = false)
    @Schema(description = "创建时间", required = true)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
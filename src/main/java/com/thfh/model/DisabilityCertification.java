package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "disability_certification")
public class DisabilityCertification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String certificateNumber; // 残疾证号码

    @Column(nullable = false)
    private String disabilityType; // 残疾类型

    @Column(nullable = false)
    private String disabilityLevel; // 残疾等级

    @Column(nullable = false)
    private String issueAuthority; // 发证机构

    @Column(nullable = false)
    private LocalDateTime issueDate; // 发证日期

    @Column(nullable = false)
    private LocalDateTime validUntil; // 有效期至

    private String certificateImage; // 残疾证图片URL

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING; // 认证状态

    private String rejectReason; // 拒绝原因（如果状态为REJECTED）

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 认证状态枚举
     */
    public enum Status {
        PENDING("待审核"),
        APPROVED("已通过"),
        REJECTED("已拒绝");

        private final String description;

        Status(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
} 
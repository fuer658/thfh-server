package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "disability_certification")
@Schema(description = "残疾证认证 - 用户的残疾证认证信息")
public class DisabilityCertification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "认证ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "用户 - 认证所属的用户", required = true)
    private User user;

    @Column(nullable = false)
    @Schema(description = "残疾证号码", required = true, example = "1234567890")
    private String certificateNumber; // 残疾证号码

    @Column(nullable = false)
    @Schema(description = "残疾类型", required = true, example = "视力障碍")
    private String disabilityType; // 残疾类型

    @Column(nullable = false)
    @Schema(description = "残疾等级", required = true, example = "一级")
    private String disabilityLevel; // 残疾等级

    @Column(nullable = false)
    @Schema(description = "发证机构", required = true, example = "北京市残疾人联合会")
    private String issueAuthority; // 发证机构

    @Column(nullable = false)
    @Schema(description = "发证日期", required = true, example = "2020-01-01T00:00:00")
    private LocalDateTime issueDate; // 发证日期

    @Column(nullable = false)
    @Schema(description = "有效期至", required = true, example = "2030-01-01T00:00:00")
    private LocalDateTime validUntil; // 有效期至

    @Schema(description = "残疾证图片URL", example = "https://example.com/certificate.jpg")
    private String certificateImage; // 残疾证图片URL

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "认证状态 - 可选值：PENDING, APPROVED, REJECTED", required = true, example = "PENDING")
    private Status status = Status.PENDING; // 认证状态

    @Schema(description = "拒绝原因 - 状态为REJECTED时的拒绝原因", example = "证件信息不清晰，请重新上传")
    private String rejectReason; // 拒绝原因（如果状态为REJECTED）

    @Column(nullable = false)
    @Schema(description = "创建时间", example = "2023-05-20T14:30:00")
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    @Schema(description = "更新时间", example = "2023-05-20T14:30:00")
    private LocalDateTime updateTime = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 认证状态枚举
     */
    @Schema(description = "认证状态枚举 - 残疾证认证的状态")
    public enum Status {
        @Schema(description = "待审核", example = "PENDING")
        PENDING("待审核"),
        
        @Schema(description = "已通过", example = "APPROVED")
        APPROVED("已通过"),
        
        @Schema(description = "已拒绝", example = "REJECTED")
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
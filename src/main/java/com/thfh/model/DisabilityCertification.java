package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "disability_certification")
@ApiModel(value = "残疾证认证", description = "用户的残疾证认证信息")
public class DisabilityCertification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "认证ID", example = "1", position = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ApiModelProperty(value = "用户", required = true, notes = "认证所属的用户", position = 2)
    private User user;

    @Column(nullable = false)
    @ApiModelProperty(value = "残疾证号码", required = true, example = "1234567890", position = 3)
    private String certificateNumber; // 残疾证号码

    @Column(nullable = false)
    @ApiModelProperty(value = "残疾类型", required = true, example = "视力障碍", position = 4)
    private String disabilityType; // 残疾类型

    @Column(nullable = false)
    @ApiModelProperty(value = "残疾等级", required = true, example = "一级", position = 5)
    private String disabilityLevel; // 残疾等级

    @Column(nullable = false)
    @ApiModelProperty(value = "发证机构", required = true, example = "北京市残疾人联合会", position = 6)
    private String issueAuthority; // 发证机构

    @Column(nullable = false)
    @ApiModelProperty(value = "发证日期", required = true, example = "2020-01-01T00:00:00", position = 7)
    private LocalDateTime issueDate; // 发证日期

    @Column(nullable = false)
    @ApiModelProperty(value = "有效期至", required = true, example = "2030-01-01T00:00:00", position = 8)
    private LocalDateTime validUntil; // 有效期至

    @ApiModelProperty(value = "残疾证图片URL", example = "https://example.com/certificate.jpg", position = 9)
    private String certificateImage; // 残疾证图片URL

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "认证状态", required = true, example = "PENDING", notes = "可选值：PENDING, APPROVED, REJECTED", position = 10)
    private Status status = Status.PENDING; // 认证状态

    @ApiModelProperty(value = "拒绝原因", example = "证件信息不清晰，请重新上传", notes = "状态为REJECTED时的拒绝原因", position = 11)
    private String rejectReason; // 拒绝原因（如果状态为REJECTED）

    @Column(nullable = false)
    @ApiModelProperty(value = "创建时间", example = "2023-05-20T14:30:00", position = 12)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    @ApiModelProperty(value = "更新时间", example = "2023-05-20T14:30:00", position = 13)
    private LocalDateTime updateTime = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 认证状态枚举
     */
    @ApiModel(value = "认证状态枚举", description = "残疾证认证的状态")
    public enum Status {
        @ApiModelProperty(value = "待审核", example = "PENDING")
        PENDING("待审核"),
        
        @ApiModelProperty(value = "已通过", example = "APPROVED")
        APPROVED("已通过"),
        
        @ApiModelProperty(value = "已拒绝", example = "REJECTED")
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
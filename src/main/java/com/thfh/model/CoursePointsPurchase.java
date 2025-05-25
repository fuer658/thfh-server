package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 积分购买课程记录实体
 * 记录用户使用积分购买课程的交易信息
 */
@Data
@Entity
@Table(name = "course_points_purchase")
@Schema(description = "课程积分购买记录 - 记录用户使用积分购买课程的详细信息")
public class CoursePointsPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "记录ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "购买用户 - 进行积分购买的用户")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @Schema(description = "购买课程 - 使用积分购买的课程")
    private Course course;

    @Column(nullable = false)
    @Schema(description = "消费积分数量", required = true, example = "500")
    private Integer pointsSpent; // 消费的积分数量

    @Column(nullable = false)
    @Schema(description = "购买状态", required = true, example = "SUCCESS")
    @Enumerated(EnumType.STRING)
    private PurchaseStatus status; // 购买状态

    @Schema(description = "交易备注", example = "首次使用积分购买课程")
    private String remark;

    @Column(nullable = false)
    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime = LocalDateTime.now();

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }

    /**
     * 购买状态枚举
     */
    public enum PurchaseStatus {
        @Schema(description = "成功", example = "SUCCESS")
        SUCCESS("交易成功"),
        
        @Schema(description = "失败", example = "FAILED")
        FAILED("交易失败"),
        
        @Schema(description = "已退款", example = "REFUNDED")
        REFUNDED("已退款");

        private final String description;

        PurchaseStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
} 
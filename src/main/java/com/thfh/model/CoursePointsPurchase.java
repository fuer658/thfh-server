package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 积分购买课程记录实体
 * 记录用户使用积分购买课程的交易信息
 */
@Data
@Entity
@Table(name = "course_points_purchase")
@ApiModel(value = "课程积分购买记录", description = "记录用户使用积分购买课程的详细信息")
public class CoursePointsPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "记录ID", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ApiModelProperty(value = "购买用户", notes = "进行积分购买的用户")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @ApiModelProperty(value = "购买课程", notes = "使用积分购买的课程")
    private Course course;

    @Column(nullable = false)
    @ApiModelProperty(value = "消费积分数量", required = true, example = "500")
    private Integer pointsSpent; // 消费的积分数量

    @Column(nullable = false)
    @ApiModelProperty(value = "购买状态", required = true, example = "SUCCESS")
    @Enumerated(EnumType.STRING)
    private PurchaseStatus status; // 购买状态

    @ApiModelProperty(value = "交易备注", example = "首次使用积分购买课程")
    private String remark;

    @Column(nullable = false)
    @ApiModelProperty(value = "创建时间", required = true)
    private LocalDateTime createTime = LocalDateTime.now();

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }

    /**
     * 购买状态枚举
     */
    public enum PurchaseStatus {
        @ApiModelProperty(value = "成功", example = "SUCCESS")
        SUCCESS("交易成功"),
        
        @ApiModelProperty(value = "失败", example = "FAILED")
        FAILED("交易失败"),
        
        @ApiModelProperty(value = "已退款", example = "REFUNDED")
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
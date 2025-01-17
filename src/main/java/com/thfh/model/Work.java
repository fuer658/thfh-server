package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "work")
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 作品标题

    @Column(length = 1000)
    private String description; // 作品描述

    @Column(nullable = false)
    private String coverImage; // 作品封面图片

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student; // 创作学员

    @Column(nullable = false)
    private BigDecimal price; // 作品价格

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkStatus status; // 作品状态

    private String images; // 作品图片，多个图片用逗号分隔

    private String video; // 作品视频

    private String materials; // 作品相关资料

    private Integer likeCount = 0; // 点赞数

    private Integer viewCount = 0; // 浏览数

    private Integer saleCount = 0; // 销售数量

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now();
} 
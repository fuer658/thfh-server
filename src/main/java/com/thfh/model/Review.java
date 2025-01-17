package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewType type; // 评价类型（课程评价/作品评价）

    @Column(nullable = false)
    private Long targetId; // 评价目标ID（课程ID/作品ID）

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 评价用户

    @Column(nullable = false)
    private Integer rating; // 评分（1-5星）

    @Column(nullable = false)
    private String content; // 评价内容

    private String images; // 评价图片，多个图片用逗号分隔

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now();
} 
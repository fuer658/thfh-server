package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "points_record")
public class PointsRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private Integer points; // 积分变动数量（正数为增加，负数为减少）

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PointsType type; // 积分变动类型

    private String description; // 变动说明

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();
} 
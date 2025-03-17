package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_course")
public class UserCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private LocalDateTime enrollTime; // 加入时间

    private LocalDateTime lastAccessTime; // 最后访问时间

    private Boolean isActive = true; // 是否在学习中

    @PrePersist
    public void prePersist() {
        enrollTime = LocalDateTime.now();
        lastAccessTime = LocalDateTime.now();
    }
}
package com.thfh.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户上传的文件实体
 */
@Data
@Entity
@Table(name = "user_file")
public class UserFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String filePath; // 文件在服务器上的相对路径

    @Column(nullable = false)
    private String fileUrl; // 文件访问URL

    @Column(nullable = false)
    private String originalFilename; // 原始文件名

    @Column(nullable = false)
    private long size; // 文件大小

    @Column(nullable = false)
    private String contentType; // 文件类型

    @Column(nullable = false)
    private LocalDateTime uploadTime = LocalDateTime.now();
}
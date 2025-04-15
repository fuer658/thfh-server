package com.thfh.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户上传文件实体类
 * 用于记录用户上传的文件信息
 */
@Data
@Entity
@Table(name = "user_upload")
public class UserUpload {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId; // 上传用户ID
    
    @Column(nullable = false, length = 255)
    private String fileName; // 文件名称
    
    @Column(nullable = false, length = 500)
    private String filePath; // 文件存储路径
    
    @Column(nullable = false, length = 255)
    private String url; // 文件访问URL
    
    @Column(nullable = false, length = 50)
    private String fileType; // 文件类型：IMAGE, VIDEO, DOCUMENT等
    
    @Column(length = 100)
    private String mimeType; // MIME类型
    
    @Column(nullable = false)
    private Long fileSize; // 文件大小(字节)
    
    @Column(length = 500)
    private String description; // 文件描述
    
    @Column(length = 100)
    private String category; // 自定义分类
    
    @Column(nullable = false)
    private LocalDateTime uploadTime = LocalDateTime.now(); // 上传时间
    
    @Column(nullable = false)
    private Boolean isPrivate = false; // 是否私有
    
    @Column(nullable = false)
    private Boolean isEnabled = true; // 是否可用
    
    @PrePersist
    public void prePersist() {
        if (uploadTime == null) {
            uploadTime = LocalDateTime.now();
        }
    }
} 
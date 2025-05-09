package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "用户上传文件", description = "记录用户上传的文件信息")
public class UserUpload {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "上传文件ID", example = "1")
    private Long id;
    
    @Column(nullable = false)
    @ApiModelProperty(value = "上传用户ID", example = "1", required = true)
    private Long userId; // 上传用户ID
    
    @Column(nullable = false, length = 255)
    @ApiModelProperty(value = "文件名称", example = "document.pdf", required = true)
    private String fileName; // 文件名称
    
    @Column(nullable = false, length = 500)
    @ApiModelProperty(value = "文件存储路径", example = "/uploads/documents/document.pdf", required = true)
    private String filePath; // 文件存储路径
    
    @Column(nullable = false, length = 255)
    @ApiModelProperty(value = "文件访问URL", example = "https://example.com/uploads/document.pdf", required = true)
    private String url; // 文件访问URL
    
    @Column(nullable = false, length = 50)
    @ApiModelProperty(value = "文件类型", example = "DOCUMENT", required = true, notes = "如IMAGE, VIDEO, DOCUMENT等")
    private String fileType; // 文件类型：IMAGE, VIDEO, DOCUMENT等
    
    @Column(length = 100)
    @ApiModelProperty(value = "MIME类型", example = "application/pdf")
    private String mimeType; // MIME类型
    
    @Column(nullable = false)
    @ApiModelProperty(value = "文件大小(字节)", example = "1024000", required = true)
    private Long fileSize; // 文件大小(字节)
    
    @Column(length = 500)
    @ApiModelProperty(value = "文件描述", example = "这是一份重要的文档")
    private String description; // 文件描述
    
    @Column(length = 100)
    @ApiModelProperty(value = "自定义分类", example = "学习资料")
    private String category; // 自定义分类
    
    @Column(nullable = false)
    @ApiModelProperty(value = "上传时间", example = "2023-01-01T10:00:00", required = true)
    private LocalDateTime uploadTime = LocalDateTime.now(); // 上传时间
    
    @Column(nullable = false)
    @ApiModelProperty(value = "是否私有", example = "false", notes = "true表示仅上传者可访问，false表示公开")
    private Boolean isPrivate = false; // 是否私有
    
    @Column(nullable = false)
    @ApiModelProperty(value = "是否可用", example = "true", notes = "false表示已被禁用或删除")
    private Boolean isEnabled = true; // 是否可用
    
    @PrePersist
    public void prePersist() {
        if (uploadTime == null) {
            uploadTime = LocalDateTime.now();
        }
    }
} 
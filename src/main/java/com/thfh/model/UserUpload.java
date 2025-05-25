package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户上传文件实体
 */
@Data
@Getter
@Setter
@Entity
@Table(name = "user_upload")
@Schema(description = "用户上传文件实体 - 存储用户上传的各类文件信息")
public class UserUpload {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "上传ID", example = "1")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "上传用户")
    private User user;
    
    @Schema(description = "文件名", example = "profile.jpg")
    private String fileName;
    
    @Schema(description = "文件路径 - 文件在服务器上的存储路径", example = "/uploads/images/2023/05/20/profile.jpg")
    private String filePath;
    
    @Schema(description = "文件URL", example = "https://example.com/uploads/images/2023/05/20/profile.jpg")
    private String fileUrl;
    
    @Schema(description = "文件类型", example = "IMAGE")
    private String fileType;
    
    @Schema(description = "文件大小(字节)", example = "1024000")
    private Long fileSize;
    
    @Schema(description = "MIME类型", example = "image/jpeg")
    private String mimeType;
    
    @Schema(description = "MD5哈希值", example = "a1b2c3d4e5f6g7h8i9j0")
    private String md5;
    
    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;
    
    @Schema(description = "最后访问时间")
    private LocalDateTime lastAccessTime;
    
    @Schema(description = "访问次数", example = "10")
    private Integer accessCount;
    
    @Schema(description = "是否公开", example = "true")
    private Boolean isPublic;
    
    @Schema(description = "关联业务类型", example = "AVATAR")
    private String businessType;
    
    @Schema(description = "关联业务ID", example = "1")
    private Long businessId;
    
    @Schema(description = "状态", example = "ACTIVE")
    private String status;
    
    @Schema(description = "备注", example = "用户头像")
    private String remark;
    
    @PrePersist
    public void prePersist() {
        if (uploadTime == null) {
            uploadTime = LocalDateTime.now();
        }
        if (lastAccessTime == null) {
            lastAccessTime = uploadTime;
        }
        if (accessCount == null) {
            accessCount = 0;
        }
        if (isPublic == null) {
            isPublic = false;
        }
        if (status == null) {
            status = "ACTIVE";
        }
    }
}

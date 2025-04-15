package com.thfh.dto;

import com.thfh.model.UserUpload;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户上传文件DTO
 */
@Data
public class UserUploadDTO {
    
    private Long id;
    private Long userId;
    private String fileName;
    private String filePath;
    private String url;
    private String fileType;
    private String mimeType;
    private Long fileSize;
    private String description;
    private String category;
    private LocalDateTime uploadTime;
    private Boolean isPrivate;
    private Boolean isEnabled;
    
    // 额外的显示字段
    private String formattedSize; // 格式化的文件大小
    private String formattedUploadTime; // 格式化的上传时间
    
    /**
     * 将实体转换为DTO
     */
    public static UserUploadDTO fromEntity(UserUpload entity) {
        if (entity == null) {
            return null;
        }
        
        UserUploadDTO dto = new UserUploadDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setFileName(entity.getFileName());
        dto.setFilePath(entity.getFilePath());
        dto.setUrl(entity.getUrl());
        dto.setFileType(entity.getFileType());
        dto.setMimeType(entity.getMimeType());
        dto.setFileSize(entity.getFileSize());
        dto.setDescription(entity.getDescription());
        dto.setCategory(entity.getCategory());
        dto.setUploadTime(entity.getUploadTime());
        dto.setIsPrivate(entity.getIsPrivate());
        dto.setIsEnabled(entity.getIsEnabled());
        
        // 格式化文件大小
        dto.setFormattedSize(formatFileSize(entity.getFileSize()));
        
        return dto;
    }
    
    /**
     * 格式化文件大小
     */
    private static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
} 
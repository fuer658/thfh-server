package com.thfh.service;

import com.thfh.model.FileType;
import com.thfh.model.User;
import com.thfh.model.UserUpload;
import com.thfh.repository.UserUploadRepository;
import com.thfh.util.ServerUrlUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class UserUploadService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    // 定义不同类型文件的MIME类型前缀
    private static final List<String> IMAGE_MIME_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp", "image/svg+xml"
    );
    
    private static final List<String> VIDEO_MIME_TYPES = Arrays.asList(
            "video/mp4", "video/quicktime", "video/x-msvideo", "video/x-matroska", "video/webm"
    );
    
    private static final List<String> AUDIO_MIME_TYPES = Arrays.asList(
            "audio/mpeg", "audio/x-wav", "audio/ogg", "audio/aac", "audio/midi"
    );
    
    private static final List<String> DOCUMENT_MIME_TYPES = Arrays.asList(
            "application/pdf", "application/msword", "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain", "text/html"
    );
    
    @Value("${file.upload-dir}")
    private String baseUploadDir;
    
    @Autowired
    private UserUploadRepository userUploadRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ServerUrlUtil serverUrlUtil;
    
    /**
     * 获取文件类型
     * @param mimeType MIME类型
     */
    private String determineFileType(String mimeType) {
        if (mimeType == null) {
            return FileType.OTHER.name();
        }
        
        String lowerMimeType = mimeType.toLowerCase();
        
        if (IMAGE_MIME_TYPES.stream().anyMatch(lowerMimeType::startsWith)) {
            return FileType.IMAGE.name();
        } else if (VIDEO_MIME_TYPES.stream().anyMatch(lowerMimeType::startsWith)) {
            return FileType.VIDEO.name();
        } else if (AUDIO_MIME_TYPES.stream().anyMatch(lowerMimeType::startsWith)) {
            return FileType.AUDIO.name();
        } else if (DOCUMENT_MIME_TYPES.stream().anyMatch(lowerMimeType::startsWith)) {
            return FileType.DOCUMENT.name();
        } else if (lowerMimeType.startsWith("application/zip") || 
                   lowerMimeType.startsWith("application/x-rar") ||
                   lowerMimeType.startsWith("application/x-7z")) {
            return FileType.ARCHIVE.name();
        } else {
            return FileType.OTHER.name();
        }
    }
    
    /**
     * 获取文件扩展名
     * @param filename 文件名
     */
    private String getFileExtension(String filename) {
        return FilenameUtils.getExtension(filename);
    }
    
    /**
     * 确定上传路径
     * @param fileType 文件类型
     * @param userId 用户ID
     */
    private String determineUploadPath(String fileType, Long userId) {
        String datePath = LocalDateTime.now().format(DATE_FORMAT);
        return userId + "/" + fileType + "/" + datePath;
    }
    
    /**
     * 生成新文件名
     * @param extension 文件扩展名
     */
    private String generateFilename(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }
    
    /**
     * 上传文件
     * @param file 上传的文件
     * @param category 分类（可选）
     * @param description 描述（可选）
     * @param isPrivate 是否私有（可选）
     * @return 上传结果
     */
    @Transactional
    public UserUpload uploadFile(MultipartFile file, String category, String description, Boolean isPrivate) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("文件不能为空");
        }
        
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new IOException("未授权，请先登录");
        }
        
        String mimeType = file.getContentType();
        String fileType = determineFileType(mimeType);
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        
        String relativePath = determineUploadPath(fileType, currentUser.getId());
        String newFilename = generateFilename(extension);
        
        // 构建实际目录路径
        Path directoryPath = Paths.get(baseUploadDir, relativePath);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
        
        // 构建文件完整路径
        Path filePath = directoryPath.resolve(newFilename);
        
        // 使用try-with-resources确保流关闭
        try {
            // 检查并清理文件流
            Files.copy(file.getInputStream(), filePath);
            
            // 尝试清理可能的临时文件
            try {
                // 使用Spring提供的MultipartFile接口清理临时资源
                file.getInputStream().close();
            } catch (Exception e) {
                // 记录但不抛出异常，避免影响主流程
                System.err.println("清理临时文件失败: " + e.getMessage());
            }
        } catch (IOException e) {
            throw new IOException("保存文件失败: " + e.getMessage(), e);
        }
        
        // 构建访问URL
        String fileUrl = serverUrlUtil.getFileUrl(relativePath + "/" + newFilename);
        
        // 创建上传记录
        UserUpload upload = new UserUpload();
        upload.setUserId(currentUser.getId());
        upload.setFileName(originalFilename);
        upload.setFilePath(relativePath + "/" + newFilename);
        upload.setUrl(fileUrl);
        upload.setFileType(fileType);
        upload.setMimeType(mimeType);
        upload.setFileSize(file.getSize());
        upload.setCategory(category);
        upload.setDescription(description);
        upload.setIsPrivate(isPrivate != null ? isPrivate : false);
        upload.setUploadTime(LocalDateTime.now());
        upload.setIsEnabled(true);
        
        // 保存到数据库
        return userUploadRepository.save(upload);
    }
    
    /**
     * 获取用户所有上传的文件
     * @param userId 用户ID
     * @return 文件列表
     */
    public List<UserUpload> getUserFiles(Long userId) {
        return userUploadRepository.findByUserIdAndIsEnabledOrderByUploadTimeDesc(userId, true);
    }
    
    /**
     * 获取用户指定类型的上传文件
     * @param userId 用户ID
     * @param fileType 文件类型
     * @return 文件列表
     */
    public List<UserUpload> getUserFilesByType(Long userId, String fileType) {
        return userUploadRepository.findByUserIdAndFileTypeAndIsEnabledOrderByUploadTimeDesc(userId, fileType, true);
    }
    
    /**
     * 获取用户指定分类的上传文件
     * @param userId 用户ID
     * @param category 分类
     * @return 文件列表
     */
    public List<UserUpload> getUserFilesByCategory(Long userId, String category) {
        return userUploadRepository.findByUserIdAndCategoryAndIsEnabledOrderByUploadTimeDesc(userId, category, true);
    }
    
    /**
     * 获取用户指定类型和分类的上传文件
     * @param userId 用户ID
     * @param fileType 文件类型
     * @param category 分类
     * @return 文件列表
     */
    public List<UserUpload> getUserFilesByTypeAndCategory(Long userId, String fileType, String category) {
        return userUploadRepository.findByUserIdAndFileTypeAndCategoryAndIsEnabledOrderByUploadTimeDesc(
                userId, fileType, category, true);
    }
    
    /**
     * 分页查询用户上传的文件
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    public Page<UserUpload> getUserFilesPage(Long userId, Pageable pageable) {
        return userUploadRepository.findByUserIdAndIsEnabled(userId, true, pageable);
    }
    
    /**
     * 搜索用户上传的文件
     * @param userId 用户ID
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 搜索结果
     */
    public Page<UserUpload> searchUserFiles(Long userId, String keyword, Pageable pageable) {
        return userUploadRepository.searchByKeyword(userId, keyword, pageable);
    }
    
    /**
     * 获取上传文件详情
     * @param fileId 文件ID
     * @return 文件信息
     */
    public UserUpload getFileById(Long fileId) {
        return userUploadRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
    }
    
    /**
     * 删除文件
     * @param fileId 文件ID
     */
    @Transactional
    public void deleteFile(Long fileId) throws IOException {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("未授权，请先登录");
        }
        
        UserUpload upload = userUploadRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
        
        // 验证文件所有权
        if (!upload.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("无权删除此文件");
        }
        
        // 物理删除文件
        Path fullPath = Paths.get(baseUploadDir, upload.getFilePath());
        if (Files.exists(fullPath)) {
            Files.delete(fullPath);
        }
        
        // 从数据库中删除记录（逻辑删除）
        upload.setIsEnabled(false);
        userUploadRepository.save(upload);
    }
    
    /**
     * 更新文件信息
     * @param fileId 文件ID
     * @param category 分类
     * @param description 描述
     * @param isPrivate 是否私有
     * @return 更新后的文件信息
     */
    @Transactional
    public UserUpload updateFileInfo(Long fileId, String category, String description, Boolean isPrivate) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("未授权，请先登录");
        }
        
        UserUpload upload = userUploadRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
        
        // 验证文件所有权
        if (!upload.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("无权修改此文件");
        }
        
        // 更新信息
        if (category != null) {
            upload.setCategory(category);
        }
        
        if (description != null) {
            upload.setDescription(description);
        }
        
        if (isPrivate != null) {
            upload.setIsPrivate(isPrivate);
        }
        
        return userUploadRepository.save(upload);
    }
} 
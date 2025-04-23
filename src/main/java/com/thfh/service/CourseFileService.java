package com.thfh.service;

import com.thfh.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 课程文件服务
 * 处理课程相关文件的上传等操作
 */
@Service
public class CourseFileService {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    // 允许的图片类型
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp", "image/svg+xml"
    );
    
    // 允许的视频类型
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/quicktime", "video/x-msvideo", "video/x-matroska", "video/webm", "video/ogg"
    );
    
    // 允许的文档类型
    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
            "application/pdf", "application/msword", 
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain"
    );
    
    @Value("${file.upload-dir}")
    private String baseUploadDir;
    
    @Value("${server.port}")
    private String serverPort;
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取服务器URL
     */
    private String getServerUrl() {
        return "http://localhost:" + serverPort;
    }
    
    /**
     * 上传课程相关文件（图片、视频、材料等）
     * @param file 上传的文件
     * @param fileType 文件类型（cover、video、material）
     * @return 文件URL
     * @throws IOException 如果上传过程中发生IO错误
     */
    public String uploadCourseFile(MultipartFile file, String fileType) throws IOException {
        return uploadCourseFile(file, fileType, null);
    }
    
    /**
     * 上传课程相关文件（图片、视频、材料等）
     * @param file 上传的文件
     * @param fileType 文件类型（cover、video、material）
     * @param description 文件描述（可选）
     * @return 文件URL
     * @throws IOException 如果上传过程中发生IO错误
     */
    public String uploadCourseFile(MultipartFile file, String fileType, String description) throws IOException {
        // 获取当前登录用户
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("未授权，请先登录");
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new RuntimeException("无法识别的文件类型");
        }
        
        // 根据文件类型进行验证
        switch (fileType) {
            case "cover":
                if (!ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
                    throw new RuntimeException("不支持的图片类型，只支持JPG、PNG、GIF、BMP、WEBP格式");
                }
                break;
            case "video":
                if (!ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase())) {
                    throw new RuntimeException("不支持的视频类型，只支持MP4、MOV、AVI、MKV、WEBM格式");
                }
                break;
            case "material":
                if (!ALLOWED_DOCUMENT_TYPES.contains(contentType.toLowerCase()) && 
                    !ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase())) {
                    throw new RuntimeException("不支持的文件类型，请上传PDF、Word、Excel、文本文档或视频文件");
                }
                break;
            default:
                throw new RuntimeException("不支持的文件类型参数");
        }
        
        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // 生成新文件名
        String newFilename = UUID.randomUUID().toString() + extension;
        
        // 获取当前日期，用于创建年月日目录结构
        String datePath = LocalDateTime.now().format(DATE_FORMAT);
        
        // 构建课程文件目录路径
        // 格式：baseUploadDir/course/fileType/YYYYMMDD/filename.ext
        String relativePath = "course/" + fileType + "/" + datePath;
        String fullPath = baseUploadDir + "/" + relativePath;
        
        // 创建目录
        Path dirPath = Paths.get(fullPath);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        
        // 保存文件
        Path filePath = dirPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath);
        
        // 生成访问URL
        return getServerUrl() + "/uploads/" + relativePath + "/" + newFilename;
    }
} 
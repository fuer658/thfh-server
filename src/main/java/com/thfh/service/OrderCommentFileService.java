package com.thfh.service;

import com.thfh.util.ServerUrlUtil;
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
 * 订单评价文件服务
 * 处理订单评价相关的文件上传
 */
@Service
public class OrderCommentFileService {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    // 允许的图片类型
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"
    );
    
    // 允许的视频类型
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/quicktime", "video/x-msvideo", "video/x-matroska", "video/webm"
    );
    
    @Value("${file.upload-dir}")
    private String baseUploadDir;
    
    @Autowired
    private ServerUrlUtil serverUrlUtil;
    
    /**
     * 上传评价图片
     * @param file 图片文件
     * @return 图片URL
     */
    public String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("文件不能为空");
        }
        
        String mimeType = file.getContentType();
        if (!ALLOWED_IMAGE_TYPES.contains(mimeType)) {
            throw new IOException("不支持的文件类型：" + mimeType);
        }
        
        return uploadFile(file, "order/comment/images");
    }
    
    /**
     * 上传评价视频
     * @param file 视频文件
     * @return 视频URL
     */
    public String uploadVideo(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("文件不能为空");
        }
        
        String mimeType = file.getContentType();
        if (!ALLOWED_VIDEO_TYPES.contains(mimeType)) {
            throw new IOException("不支持的文件类型：" + mimeType);
        }
        
        return uploadFile(file, "order/comment/videos");
    }
    
    /**
     * 上传文件
     * @param file 文件
     * @param subDir 子目录
     * @return 文件URL
     */
    private String uploadFile(MultipartFile file, String subDir) throws IOException {
        // 获取当前日期，用于创建年月日目录结构
        String datePath = LocalDateTime.now().format(DATE_FORMAT);
        
        // 构建文件目录路径
        // 格式：baseUploadDir/order/comment/images|videos/YYYYMMDD/filename.ext
        String relativePath = subDir + "/" + datePath;
        String fullPath = baseUploadDir + "/" + relativePath;
        
        // 创建目录
        Path dirPath = Paths.get(fullPath);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension;
        
        // 保存文件
        Path filePath = dirPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath);
        
        // 生成访问URL
        return serverUrlUtil.getFileUrl(relativePath + "/" + newFilename);
    }
    
    /**
     * 删除文件
     * @param fileUrl 文件URL
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            // 从URL中提取文件名
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(baseUploadDir, fileName);
            
            // 删除文件
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("删除文件失败: " + e.getMessage());
        }
    }
} 
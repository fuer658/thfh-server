package com.thfh.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Arrays;
import java.util.List;

/**
 * 文件上传控制器
 * 提供文件上传功能，支持图片、视频等多种类型文件的上传
 * 支持自定义存储路径和文件名
 */
@RestController
@RequestMapping("/api")
public class FileController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${server.port}")
    private String serverPort;

    // 允许的视频文件类型
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
        "video/mp4",
        "video/quicktime",
        "video/x-msvideo",
        "video/x-matroska"
    );

    // 最大视频文件大小（100MB）
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024;

    /**
     * 获取服务器URL
     * @return 服务器URL地址
     */
    private String getServerUrl() {
        return "http://localhost:" + serverPort;
    }

    /**
     * 验证文件是否为允许的视频类型
     * @param file 上传的文件
     * @return 是否为允许的视频类型
     */
    private boolean isValidVideoType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase());
    }

    /**
     * 处理文件保存
     * @param file 上传的文件
     * @param customPath 自定义路径（可选）
     * @param customFileName 自定义文件名（可选）
     * @return 保存后的文件路径和URL
     */
    private Map<String, String> saveFile(MultipartFile file, String customPath, String customFileName) throws IOException {
        // 确定存储路径
        String finalPath = customPath != null && !customPath.trim().isEmpty() 
            ? uploadDir + "/" + customPath.trim() 
            : uploadDir;
        
        // 创建目录（如果不存在）
        Path uploadPath = Paths.get(finalPath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 确定文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = customFileName != null && !customFileName.trim().isEmpty()
            ? customFileName.trim() + extension
            : UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        // 构建相对路径和完整URL
        String relativePath = (customPath != null ? customPath + "/" : "") + filename;
        String fileUrl = getServerUrl() + "/uploads/" + relativePath;

        Map<String, String> result = new HashMap<>();
        result.put("path", relativePath);
        result.put("url", fileUrl);
        return result;
    }

    /**
     * 文件上传接口
     * 支持各种类型文件的上传，可自定义存储路径和文件名
     * 
     * @param file 上传的文件
     * @param customPath 自定义存储路径（可选）
     * @param customFileName 自定义文件名（可选，不包含扩展名）
     * @return 上传结果，包含文件访问URL
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "path", required = false) String customPath,
            @RequestParam(value = "filename", required = false) String customFileName) {
        try {
            Map<String, String> fileInfo = saveFile(file, customPath, customFileName);
            
            Map<String, Object> data = new HashMap<>();
            data.put("code", 200);
            data.put("url", fileInfo.get("url"));
            data.put("path", fileInfo.get("path"));
            data.put("message", "上传成功");
            return ResponseEntity.ok(data);

        } catch (IOException e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 视频文件上传接口
     * 专门用于上传视频文件，支持大文件上传，可自定义存储路径和文件名
     * 
     * @param file 上传的视频文件
     * @param customPath 自定义存储路径（可选）
     * @param customFileName 自定义文件名（可选，不包含扩展名）
     * @return 上传结果，包含视频访问URL
     */
    @PostMapping("/upload/video")
    public ResponseEntity<?> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "path", required = false) String customPath,
            @RequestParam(value = "filename", required = false) String customFileName) {
        try {
            // 验证文件类型
            if (!isValidVideoType(file)) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 400);
                error.put("message", "不支持的文件类型，仅支持MP4、MOV、AVI、MKV格式的视频");
                return ResponseEntity.badRequest().body(error);
            }

            // 验证文件大小
            if (file.getSize() > MAX_VIDEO_SIZE) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 400);
                error.put("message", "视频文件大小不能超过100MB");
                return ResponseEntity.badRequest().body(error);
            }

            Map<String, String> fileInfo = saveFile(file, customPath, customFileName);
            
            Map<String, Object> data = new HashMap<>();
            data.put("code", 200);
            data.put("url", fileInfo.get("url"));
            data.put("path", fileInfo.get("path"));
            data.put("message", "视频上传成功");
            data.put("size", file.getSize());
            data.put("contentType", file.getContentType());
            return ResponseEntity.ok(data);

        } catch (IOException e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "视频上传失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

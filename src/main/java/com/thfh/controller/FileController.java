package com.thfh.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
     * 文件上传接口
     * 支持各种类型文件的上传，生成唯一文件名并返回访问URL
     * 
     * @param file 上传的文件
     * @return 上传结果，包含文件访问URL
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 创建上传目录（如果不存在）
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一的文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // 返回完整的文件访问URL
            String fileUrl = getServerUrl() + "/uploads/" + filename;
            Map<String, Object> data = new HashMap<>();
            data.put("code", 200);
            data.put("url", fileUrl);
            data.put("message", "上传成功");
            return ResponseEntity.ok(data);

        } catch (IOException e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "文件上传失败");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 视频文件上传接口
     * 专门用于上传视频文件，支持大文件上传
     * 
     * @param file 上传的视频文件
     * @return 上传结果，包含视频访问URL
     */
    @PostMapping("/upload/video")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file) {
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

            // 创建上传目录（如果不存在）
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一的文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // 返回完整的文件访问URL
            String fileUrl = getServerUrl() + "/uploads/" + filename;
            Map<String, Object> data = new HashMap<>();
            data.put("code", 200);
            data.put("url", fileUrl);
            data.put("message", "视频上传成功");
            data.put("size", file.getSize());
            data.put("contentType", file.getContentType());
            return ResponseEntity.ok(data);

        } catch (IOException e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "视频上传失败");
            return ResponseEntity.badRequest().body(error);
        }
    }
}

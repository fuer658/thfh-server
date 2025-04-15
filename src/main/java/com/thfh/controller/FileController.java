package com.thfh.controller;

import com.thfh.model.User;
import com.thfh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件上传控制器
 * 提供文件上传功能，支持图片、视频等多种类型文件的上传
 * 支持自定义存储路径和文件名
 * 支持获取用户文件和删除文件
 */
@RestController
@RequestMapping("/api")
public class FileController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${server.port}")
    private String serverPort;
    
    @Autowired
    private UserService userService;

    // 允许的视频文件类型
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
        "video/mp4",
        "video/quicktime",
        "video/x-msvideo",
        "video/x-matroska"
    );

    // 最大视频文件大小（100MB）
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024;

    // 存储用户文件信息的Map，key为用户ID，value为文件信息列表
    private final Map<Long, List<FileInfo>> userFiles = new HashMap<>();

    /**
     * 文件信息内部类
     */
    private static class FileInfo {
        private final String path;
        private final String url;
        private final String originalFilename;
        private final long size;
        private final String contentType;
        private final Date uploadTime;

        public FileInfo(String path, String url, String originalFilename, long size, String contentType) {
            this.path = path;
            this.url = url;
            this.originalFilename = originalFilename;
            this.size = size;
            this.contentType = contentType;
            this.uploadTime = new Date();
        }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("path", path);
            map.put("url", url);
            map.put("originalFilename", originalFilename);
            map.put("size", size);
            map.put("contentType", contentType);
            map.put("uploadTime", uploadTime);
            return map;
        }
    }

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
    private FileInfo saveFile(MultipartFile file, String customPath, String customFileName) throws IOException {
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

        return new FileInfo(relativePath, fileUrl, originalFilename, file.getSize(), file.getContentType());
    }

    /**
     * 添加文件到用户文件列表
     * @param userId 用户ID
     * @param fileInfo 文件信息
     */
    private void addFileToUserFiles(Long userId, FileInfo fileInfo) {
        userFiles.computeIfAbsent(userId, k -> new ArrayList<>()).add(fileInfo);
    }

    /**
     * 从用户文件列表中移除文件
     * @param userId 用户ID
     * @param filePath 文件路径
     * @return 是否成功移除
     */
    private boolean removeFileFromUserFiles(Long userId, String filePath) {
        List<FileInfo> files = userFiles.get(userId);
        if (files != null) {
            return files.removeIf(file -> file.path.equals(filePath));
        }
        return false;
    }

    /**
     * 文件上传接口
     * 支持各种类型文件的上传，可自定义存储路径和文件名
     * 需要用户认证
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
            // 获取当前登录用户
            User currentUser = userService.getCurrentUser();
            
            // 检查用户是否已认证
            if (currentUser == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 401);
                error.put("message", "未授权，请先登录");
                return ResponseEntity.status(401).body(error);
            }
            
            FileInfo fileInfo = saveFile(file, customPath, customFileName);
            
            // 添加到用户文件列表
            addFileToUserFiles(currentUser.getId(), fileInfo);
            
            Map<String, Object> data = new HashMap<>();
            data.put("code", 200);
            data.put("url", fileInfo.url);
            data.put("path", fileInfo.path);
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
     * 需要用户认证
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
            // 获取当前登录用户
            User currentUser = userService.getCurrentUser();
            
            // 检查用户是否已认证
            if (currentUser == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 401);
                error.put("message", "未授权，请先登录");
                return ResponseEntity.status(401).body(error);
            }
            
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

            FileInfo fileInfo = saveFile(file, customPath, customFileName);
            
            // 添加到用户文件列表
            addFileToUserFiles(currentUser.getId(), fileInfo);
            
            Map<String, Object> data = new HashMap<>();
            data.put("code", 200);
            data.put("url", fileInfo.url);
            data.put("path", fileInfo.path);
            data.put("message", "视频上传成功");
            data.put("size", fileInfo.size);
            data.put("contentType", fileInfo.contentType);
            return ResponseEntity.ok(data);

        } catch (IOException e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "视频上传失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 获取当前用户的文件列表
     * 需要用户认证
     * 
     * @return 文件列表
     */
    @GetMapping("/files")
    public ResponseEntity<?> getUserFiles() {
        // 获取当前登录用户
        User currentUser = userService.getCurrentUser();
        
        // 检查用户是否已认证
        if (currentUser == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 401);
            error.put("message", "未授权，请先登录");
            return ResponseEntity.status(401).body(error);
        }
        
        List<FileInfo> files = userFiles.getOrDefault(currentUser.getId(), new ArrayList<>());
        
        List<Map<String, Object>> fileList = files.stream()
                .map(FileInfo::toMap)
                .collect(Collectors.toList());
        
        Map<String, Object> data = new HashMap<>();
        data.put("code", 200);
        data.put("files", fileList);
        data.put("message", "获取成功");
        return ResponseEntity.ok(data);
    }

    /**
     * 删除文件
     * 需要用户认证
     * 
     * @param filePath 文件路径
     * @return 操作结果
     */
    @DeleteMapping("/files")
    public ResponseEntity<?> deleteFile(@RequestParam("path") String filePath) {
        try {
            // 获取当前登录用户
            User currentUser = userService.getCurrentUser();
            
            // 检查用户是否已认证
            if (currentUser == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 401);
                error.put("message", "未授权，请先登录");
                return ResponseEntity.status(401).body(error);
            }
            
            Long userId = currentUser.getId();
            
            // 检查文件是否属于当前用户
            List<FileInfo> userFileList = userFiles.get(userId);
            if (userFileList == null || !userFileList.stream().anyMatch(file -> file.path.equals(filePath))) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 403);
                error.put("message", "无权删除此文件");
                return ResponseEntity.badRequest().body(error);
            }
            
            // 删除物理文件
            Path fileFullPath = Paths.get(uploadDir, filePath);
            if (Files.exists(fileFullPath)) {
                Files.delete(fileFullPath);
            }
            
            // 从用户文件列表中移除
            removeFileFromUserFiles(userId, filePath);
            
            Map<String, Object> data = new HashMap<>();
            data.put("code", 200);
            data.put("message", "文件删除成功");
            return ResponseEntity.ok(data);
            
        } catch (IOException e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "文件删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

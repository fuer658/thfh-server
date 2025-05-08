package com.thfh.controller;

import com.thfh.model.User;
import com.thfh.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags = "文件管理", description = "提供文件上传、查询、删除等功能，支持图片、视频等多种类型文件")
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

    // 允许的图片文件类型
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/bmp",
        "image/webp"
    );

    // 最大视频文件大小（100MB）
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024;
    
    // 最大图片文件大小（10MB）
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;

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
     * 验证文件是否为允许的图片类型
     * @param file 上传的文件
     * @return 是否为允许的图片类型
     */
    private boolean isValidImageType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
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
    @ApiOperation(value = "上传通用文件", notes = "上传各种类型的文件，支持自定义存储路径和文件名")
    @ApiResponses({
        @ApiResponse(code = 200, message = "上传成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @ApiParam(value = "上传的文件", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "自定义存储路径（可选）", required = false) @RequestParam(value = "path", required = false) String customPath,
            @ApiParam(value = "自定义文件名（可选，不包含扩展名）", required = false) @RequestParam(value = "filename", required = false) String customFileName) {
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
    @ApiOperation(value = "上传视频文件", notes = "专门用于上传视频文件，支持大文件上传，仅支持MP4、MOV、AVI、MKV格式")
    @ApiResponses({
        @ApiResponse(code = 200, message = "上传成功"),
        @ApiResponse(code = 400, message = "请求参数错误或不支持的文件类型"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PostMapping("/upload/video")
    public ResponseEntity<?> uploadVideo(
            @ApiParam(value = "上传的视频文件", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "自定义存储路径（可选）", required = false) @RequestParam(value = "path", required = false) String customPath,
            @ApiParam(value = "自定义文件名（可选，不包含扩展名）", required = false) @RequestParam(value = "filename", required = false) String customFileName) {
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
     * 获取当前用户上传的所有文件列表
     * 
     * @return 用户文件列表，包含URL、原始文件名等信息
     */
    @ApiOperation(value = "获取用户文件列表", notes = "获取当前登录用户上传的所有文件列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
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
        
        List<Map<String, Object>> filesList = new ArrayList<>();
        List<FileInfo> files = userFiles.get(currentUser.getId());
        
        if (files != null) {
            filesList = files.stream()
                .map(FileInfo::toMap)
                .collect(Collectors.toList());
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("code", 200);
        data.put("files", filesList);
        data.put("totalCount", filesList.size());
        return ResponseEntity.ok(data);
    }

    /**
     * 删除文件
     * 
     * @param filePath 文件路径
     * @return 删除结果
     */
    @ApiOperation(value = "删除文件", notes = "删除指定文件，需要提供文件路径")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "文件不存在"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @DeleteMapping("/files")
    public ResponseEntity<?> deleteFile(
            @ApiParam(value = "文件路径", required = true) @RequestParam("path") String filePath) {
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
            
            // 尝试从用户文件列表中移除
            boolean removed = removeFileFromUserFiles(currentUser.getId(), filePath);
            if (!removed) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 404);
                error.put("message", "文件不存在或您没有权限删除");
                return ResponseEntity.status(404).body(error);
            }
            
            // 构建完整文件路径
            Path fullPath = Paths.get(uploadDir, filePath);
            
            // 检查文件是否存在
            if (!Files.exists(fullPath)) {
                Map<String, Object> data = new HashMap<>();
                data.put("code", 200);
                data.put("message", "文件记录已删除，但文件不存在于磁盘");
                return ResponseEntity.ok(data);
            }
            
            // 删除文件
            Files.delete(fullPath);
            
            Map<String, Object> data = new HashMap<>();
            data.put("code", 200);
            data.put("message", "文件删除成功");
            return ResponseEntity.ok(data);
            
        } catch (IOException e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "文件删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 批量上传图片文件
     * 支持同时上传多张图片，验证图片类型和大小，可自定义存储路径
     * 需要用户认证
     * 
     * @param files 上传的图片文件数组
     * @param customPath 自定义存储路径（可选）
     * @return 上传结果，包含所有图片的访问URL
     */
    @ApiOperation(value = "批量上传图片", notes = "支持同时上传多张图片，仅支持JPG、PNG、GIF、BMP、WEBP格式，单张图片不超过10MB")
    @ApiResponses({
        @ApiResponse(code = 200, message = "上传成功"),
        @ApiResponse(code = 400, message = "请求参数错误或不支持的文件类型"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PostMapping("/upload/images")
    public ResponseEntity<?> uploadMultipleImages(
            @ApiParam(value = "上传的图片文件数组", required = true) @RequestParam("files") MultipartFile[] files,
            @ApiParam(value = "自定义存储路径（可选）", required = false) @RequestParam(value = "path", required = false) String customPath) {
        
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
            
            // 检查是否有文件上传
            if (files == null || files.length == 0) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", 400);
                error.put("message", "请选择要上传的图片");
                return ResponseEntity.badRequest().body(error);
            }
            
            List<Map<String, Object>> uploadedFiles = new ArrayList<>();
            List<String> errorMessages = new ArrayList<>();
            
            for (MultipartFile file : files) {
                // 跳过空文件
                if (file.isEmpty()) {
                    continue;
                }
                
                // 验证文件类型
                if (!isValidImageType(file)) {
                    errorMessages.add(file.getOriginalFilename() + ": 不支持的文件类型，仅支持JPG、PNG、GIF、BMP、WEBP格式的图片");
                    continue;
                }
                
                // 验证文件大小
                if (file.getSize() > MAX_IMAGE_SIZE) {
                    errorMessages.add(file.getOriginalFilename() + ": 图片大小不能超过10MB");
                    continue;
                }
                
                try {
                    // 使用UUID作为文件名
                    FileInfo fileInfo = saveFile(file, customPath, null);
                    
                    // 添加到用户文件列表
                    addFileToUserFiles(currentUser.getId(), fileInfo);
                    
                    // 添加到上传成功列表
                    Map<String, Object> fileData = new HashMap<>();
                    fileData.put("url", fileInfo.url);
                    fileData.put("path", fileInfo.path);
                    fileData.put("originalFilename", fileInfo.originalFilename);
                    fileData.put("size", fileInfo.size);
                    uploadedFiles.add(fileData);
                } catch (IOException e) {
                    errorMessages.add(file.getOriginalFilename() + ": 上传失败 - " + e.getMessage());
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "图片上传完成");
            response.put("success", uploadedFiles.size());
            response.put("failed", errorMessages.size());
            response.put("files", uploadedFiles);
            
            if (!errorMessages.isEmpty()) {
                response.put("errors", errorMessages);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "图片上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}

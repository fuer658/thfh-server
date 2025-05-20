package com.thfh.controller;

import com.thfh.model.User;
import com.thfh.service.UserService;
import com.thfh.util.ServerUrlUtil;
import com.thfh.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import java.util.Collections;

/**
 * 文件上传控制器
 * 提供文件上传功能，支持图片、视频等多种类型文件的上传
 * 支持自定义存储路径和文件名
 * 支持获取用户文件和删除文件
 */
@Api(tags = "文件管理")
@RestController
@RequestMapping("/api")
public class FileController {

    private static final String MESSAGE = "message";
    private static final String UNAUTHORIZED_MSG = "未授权，请先登录";
    private static final String PATH_DELIMITER = java.io.File.separator;

    @Value("${file.upload-dir}")
    private String uploadDir;
    
    private final UserService userService;
    private final ServerUrlUtil serverUrlUtil;

    public FileController(UserService userService, ServerUrlUtil serverUrlUtil) {
        this.userService = userService;
        this.serverUrlUtil = serverUrlUtil;
    }

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
        "image/jpg",
        "image/png",
        "image/gif",
        "image/bmp",
        "image/webp"
    );

    // 最大视频文件大小（100MB）
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024L;
    
    // 最大图片文件大小（10MB）
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024L;

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
            ? uploadDir + PATH_DELIMITER + customPath.trim() 
            : uploadDir;
        
        // 创建目录（如果不存在）
        Path uploadPath = Paths.get(finalPath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 确定文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = customFileName != null && !customFileName.trim().isEmpty()
            ? customFileName.trim() + extension
            : UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        // 构建相对路径和完整URL
        String relativePath = (customPath != null && !customPath.isEmpty() ? customPath + PATH_DELIMITER : "") + filename;
        String fileUrl = serverUrlUtil.getFileUrl(relativePath.replace(PATH_DELIMITER, "/"));

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
    public Result<Map<String, Object>> uploadFile(
            @ApiParam(value = "上传的文件", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "自定义存储路径（可选）", required = false) @RequestParam(value = "path", required = false) String customPath,
            @ApiParam(value = "自定义文件名（可选，不包含扩展名）", required = false) @RequestParam(value = "filename", required = false) String customFileName) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return Result.error(401, UNAUTHORIZED_MSG);
            }
            FileInfo fileInfo = saveFile(file, customPath, customFileName);
            addFileToUserFiles(currentUser.getId(), fileInfo);
            Map<String, Object> data = new HashMap<>();
            data.put("url", fileInfo.url);
            data.put("path", fileInfo.path);
            data.put(MESSAGE, "上传成功");
            return Result.success(data);
        } catch (IOException e) {
            return Result.error(500, "文件上传失败: " + e.getMessage());
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
    public Result<Map<String, Object>> uploadVideo(
            @ApiParam(value = "上传的视频文件", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "自定义存储路径（可选）", required = false) @RequestParam(value = "path", required = false) String customPath,
            @ApiParam(value = "自定义文件名（可选，不包含扩展名）", required = false) @RequestParam(value = "filename", required = false) String customFileName) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return Result.error(401, UNAUTHORIZED_MSG);
            }
            if (!isValidVideoType(file)) {
                return Result.error(400, "不支持的文件类型，仅支持MP4、MOV、AVI、MKV格式的视频");
            }
            if (file.getSize() > MAX_VIDEO_SIZE) {
                return Result.error(400, "视频文件大小不能超过100MB");
            }
            FileInfo fileInfo = saveFile(file, customPath, customFileName);
            addFileToUserFiles(currentUser.getId(), fileInfo);
            Map<String, Object> data = new HashMap<>();
            data.put("url", fileInfo.url);
            data.put("path", fileInfo.path);
            data.put(MESSAGE, "视频上传成功");
            data.put("size", fileInfo.size);
            data.put("contentType", fileInfo.contentType);
            return Result.success(data);
        } catch (IOException e) {
            return Result.error(500, "视频上传失败: " + e.getMessage());
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
    public Result<Map<String, Object>> getUserFiles() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return Result.error(401, UNAUTHORIZED_MSG);
        }
        List<Map<String, Object>> filesList = new ArrayList<>();
        List<FileInfo> files = userFiles.get(currentUser.getId());
        if (files != null) {
            filesList = files.stream().map(FileInfo::toMap).collect(Collectors.toList());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("files", filesList);
        data.put("totalCount", filesList.size());
        return Result.success(data);
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
    public Result<Map<String, Object>> deleteFile(
            @ApiParam(value = "文件路径", required = true) @RequestParam("path") String filePath) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return Result.error(401, UNAUTHORIZED_MSG);
            }
            boolean removed = removeFileFromUserFiles(currentUser.getId(), filePath);
            if (!removed) {
                return Result.error(404, "文件不存在或您没有权限删除");
            }
            Path fullPath = Paths.get(uploadDir, filePath);
            if (!Files.exists(fullPath)) {
                Map<String, Object> data = new HashMap<>();
                data.put(MESSAGE, "文件记录已删除，但文件不存在于磁盘");
                return Result.success(data);
            }
            Files.delete(fullPath);
            Map<String, Object> data = new HashMap<>();
            data.put(MESSAGE, "文件删除成功");
            return Result.success(data);
        } catch (IOException e) {
            return Result.error(500, "文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 处理单张图片的上传、校验和保存
     */
    private Map<String, Object> processSingleImage(MultipartFile file, String customPath, Long userId, List<String> errorMessages) {
        Map<String, Object> fileData = new HashMap<>();
        if (file.isEmpty() || !isValidImageType(file) || file.getSize() > MAX_IMAGE_SIZE) {
            String errMsg = file.getOriginalFilename() + ": ";
            if (file.isEmpty()) {
                errMsg += "文件为空";
            } else if (!isValidImageType(file)) {
                errMsg += "不支持的文件类型，仅支持JPG、PNG、GIF、BMP、WEBP格式的图片";
            } else {
                errMsg += "图片大小不能超过10MB";
            }
            errorMessages.add(errMsg);
            return Collections.emptyMap();
        }
        try {
            FileInfo fileInfo = saveFile(file, customPath, null);
            addFileToUserFiles(userId, fileInfo);
            fileData.put("url", fileInfo.url);
            fileData.put("path", fileInfo.path);
            fileData.put("originalFilename", fileInfo.originalFilename);
            fileData.put("size", fileInfo.size);
        } catch (IOException e) {
            errorMessages.add(file.getOriginalFilename() + ": 上传失败 - " + e.getMessage());
            return Collections.emptyMap();
        }
        return fileData;
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
    public Result<Map<String, Object>> uploadMultipleImages(
            @ApiParam(value = "上传的图片文件数组", required = true) @RequestParam("files") MultipartFile[] files,
            @ApiParam(value = "自定义存储路径（可选）", required = false) @RequestParam(value = "path", required = false) String customPath) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return Result.error(401, UNAUTHORIZED_MSG);
            }
            if (files == null || files.length == 0) {
                return Result.error(400, "请选择要上传的图片");
            }
            List<Map<String, Object>> uploadedFiles = new ArrayList<>();
            List<String> errorMessages = new ArrayList<>();
            for (MultipartFile file : files) {
                Map<String, Object> fileData = processSingleImage(file, customPath, currentUser.getId(), errorMessages);
                if (fileData != null) {
                    uploadedFiles.add(fileData);
                }
            }
            Map<String, Object> response = new HashMap<>();
            response.put(MESSAGE, "图片上传完成");
            response.put("success", uploadedFiles.size());
            response.put("failed", errorMessages.size());
            response.put("files", uploadedFiles);
            if (!errorMessages.isEmpty()) {
                response.put("errors", errorMessages);
            }
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(500, "图片上传失败: " + e.getMessage());
        }
    }
}

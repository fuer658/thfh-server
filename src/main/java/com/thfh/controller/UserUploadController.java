package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.UserUploadDTO;
import com.thfh.model.User;
import com.thfh.model.UserUpload;
import com.thfh.service.UserService;
import com.thfh.service.UserUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/uploads")
public class UserUploadController {
    
    @Autowired
    private UserUploadService uploadService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 上传文件
     * @param file 文件
     * @param category 分类（可选）
     * @param description 描述（可选）
     * @param isPrivate 是否私有（可选）
     * @return 上传结果
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<UserUploadDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isPrivate", required = false) Boolean isPrivate) {
        try {
            UserUpload upload = uploadService.uploadFile(file, category, description, isPrivate);
            return Result.success(UserUploadDTO.fromEntity(upload));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取当前用户的所有上传文件
     * @return 文件列表
     */
    @GetMapping
    public Result<List<UserUploadDTO>> getUserFiles() {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return Result.error("未授权，请先登录");
            }
            
            List<UserUpload> uploads = uploadService.getUserFiles(currentUser.getId());
            List<UserUploadDTO> dtoList = uploads.stream()
                    .map(UserUploadDTO::fromEntity)
                    .collect(Collectors.toList());
            
            return Result.success(dtoList);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 按文件类型获取用户上传文件
     * @param fileType 文件类型
     * @return 文件列表
     */
    @GetMapping("/type/{fileType}")
    public Result<List<UserUploadDTO>> getUserFilesByType(@PathVariable String fileType) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return Result.error("未授权，请先登录");
            }
            
            List<UserUpload> uploads = uploadService.getUserFilesByType(currentUser.getId(), fileType);
            List<UserUploadDTO> dtoList = uploads.stream()
                    .map(UserUploadDTO::fromEntity)
                    .collect(Collectors.toList());
            
            return Result.success(dtoList);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 按分类获取用户上传文件
     * @param category 分类
     * @return 文件列表
     */
    @GetMapping("/category/{category}")
    public Result<List<UserUploadDTO>> getUserFilesByCategory(@PathVariable String category) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return Result.error("未授权，请先登录");
            }
            
            List<UserUpload> uploads = uploadService.getUserFilesByCategory(currentUser.getId(), category);
            List<UserUploadDTO> dtoList = uploads.stream()
                    .map(UserUploadDTO::fromEntity)
                    .collect(Collectors.toList());
            
            return Result.success(dtoList);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页获取用户上传文件
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<Page<UserUploadDTO>> getUserFilesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return Result.error("未授权，请先登录");
            }
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadTime"));
            Page<UserUpload> uploadPage = uploadService.getUserFilesPage(currentUser.getId(), pageable);
            Page<UserUploadDTO> dtoPage = uploadPage.map(UserUploadDTO::fromEntity);
            
            return Result.success(dtoPage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 搜索用户上传文件
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    @GetMapping("/search")
    public Result<Page<UserUploadDTO>> searchUserFiles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return Result.error("未授权，请先登录");
            }
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadTime"));
            Page<UserUpload> uploadPage = uploadService.searchUserFiles(currentUser.getId(), keyword, pageable);
            Page<UserUploadDTO> dtoPage = uploadPage.map(UserUploadDTO::fromEntity);
            
            return Result.success(dtoPage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取上传文件详情
     * @param id 文件ID
     * @return 文件详情
     */
    @GetMapping("/{id}")
    public Result<UserUploadDTO> getUploadDetail(@PathVariable Long id) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return Result.error("未授权，请先登录");
            }
            
            UserUpload upload = uploadService.getFileById(id);
            
            // 验证文件所有权
            if (!upload.getUserId().equals(currentUser.getId())) {
                return Result.error("无权访问此文件");
            }
            
            return Result.success(UserUploadDTO.fromEntity(upload));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除上传文件
     * @param id 文件ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUpload(@PathVariable Long id) {
        try {
            uploadService.deleteFile(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新文件信息
     * @param id 文件ID
     * @param category 分类
     * @param description 描述
     * @param isPrivate 是否私有
     * @return 更新后的文件信息
     */
    @PutMapping("/{id}")
    public Result<UserUploadDTO> updateUpload(
            @PathVariable Long id,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Boolean isPrivate) {
        try {
            UserUpload upload = uploadService.updateFileInfo(id, category, description, isPrivate);
            return Result.success(UserUploadDTO.fromEntity(upload));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
} 
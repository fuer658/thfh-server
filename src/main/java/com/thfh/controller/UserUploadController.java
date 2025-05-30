package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.UserUploadDTO;
import com.thfh.model.User;
import com.thfh.model.UserUpload;
import com.thfh.service.UserService;
import com.thfh.service.UserUploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.thfh.exception.UserNotLoggedInException;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户上传管理控制器
 * 提供用户上传文件相关的API接口
 */
@Tag(name = "用户上传管理", description = "用户上传文件相关的API接口")
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
    /**
     * 上传文件
     * @param file 文件
     * @param category 分类（可选）
     * @param description 描述（可选）
     * @param isPrivate 是否私有（可选）
     * @return 上传结果
     */
    @Operation(summary = "上传文件", description = "用户上传文件")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "上传成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<UserUploadDTO> uploadFile(
            @Parameter(description = "上传文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "文件分类") @RequestParam(value = "category", required = false) String category,
            @Parameter(description = "文件描述") @RequestParam(value = "description", required = false) String description,
            @Parameter(description = "是否私有") @RequestParam(value = "isPrivate", required = false) Boolean isPrivate) throws IOException {
        UserUpload upload = uploadService.uploadFile(file, category, description, isPrivate);
        return Result.success(UserUploadDTO.fromEntity(upload));
    }
    
    /**
     * 获取当前用户的所有上传文件
     * @return 文件列表
     */
    /**
     * 获取当前用户的所有上传文件
     * @return 文件列表
     */
    @Operation(summary = "获取当前用户的所有上传文件", description = "获取当前登录用户上传的所有文件列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping
    public Result<List<UserUploadDTO>> getUserFiles() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new UserNotLoggedInException("未授权，请先登录");
        }
        List<UserUpload> uploads = uploadService.getUserFiles(currentUser.getId());
        List<UserUploadDTO> dtoList = uploads.stream()
                .map(UserUploadDTO::fromEntity)
                .collect(Collectors.toList());
        return Result.success(dtoList);
    }
    
    /**
     * 按文件类型获取用户上传文件
     * @param fileType 文件类型
     * @return 文件列表
     */
    /**
     * 按文件类型获取用户上传文件
     * @param fileType 文件类型
     * @return 文件列表
     */
    @Operation(summary = "按文件类型获取用户上传文件", description = "根据文件类型获取当前登录用户上传的文件列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/type/{fileType}")
    public Result<List<UserUploadDTO>> getUserFilesByType(
            @Parameter(description = "文件类型", required = true) @PathVariable String fileType) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new UserNotLoggedInException("未授权，请先登录");
        }
        List<UserUpload> uploads = uploadService.getUserFilesByType(currentUser.getId(), fileType);
        List<UserUploadDTO> dtoList = uploads.stream()
                .map(UserUploadDTO::fromEntity)
                .collect(Collectors.toList());
        return Result.success(dtoList);
    }
    
    /**
     * 按分类获取用户上传文件
     * @param category 分类
     * @return 文件列表
     */
    /**
     * 按分类获取用户上传文件
     * @param category 分类
     * @return 文件列表
     */
    @Operation(summary = "按分类获取用户上传文件", description = "根据分类获取当前登录用户上传的文件列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/category/{category}")
    public Result<List<UserUploadDTO>> getUserFilesByCategory(
            @Parameter(description = "文件分类", required = true) @PathVariable String category) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new UserNotLoggedInException("未授权，请先登录");
        }
        List<UserUpload> uploads = uploadService.getUserFilesByCategory(currentUser.getId(), category);
        List<UserUploadDTO> dtoList = uploads.stream()
                .map(UserUploadDTO::fromEntity)
                .collect(Collectors.toList());
        return Result.success(dtoList);
    }
    
    /**
     * 分页获取用户上传文件
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    /**
     * 分页获取用户上传文件
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页获取用户上传文件", description = "分页获取当前登录用户上传的文件列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/page")
    public Result<Page<UserUploadDTO>> getUserFilesPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new UserNotLoggedInException("未授权，请先登录");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadTime"));
        Page<UserUpload> uploadPage = uploadService.getUserFilesPage(currentUser.getId(), pageable);
        Page<UserUploadDTO> dtoPage = uploadPage.map(UserUploadDTO::fromEntity);
        return Result.success(dtoPage);
    }
    
    /**
     * 搜索用户上传文件
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    /**
     * 搜索用户上传文件
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    @Operation(summary = "搜索用户上传文件", description = "根据关键词搜索当前登录用户上传的文件")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/search")
    public Result<Page<UserUploadDTO>> searchUserFiles(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new UserNotLoggedInException("未授权，请先登录");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadTime"));
        Page<UserUpload> uploadPage = uploadService.searchUserFiles(currentUser.getId(), keyword, pageable);
        Page<UserUploadDTO> dtoPage = uploadPage.map(UserUploadDTO::fromEntity);
        return Result.success(dtoPage);
    }
    
    /**
     * 获取上传文件详情
     * @param id 文件ID
     * @return 文件详情
     */
    /**
     * 获取上传文件详情
     * @param id 文件ID
     * @return 文件详情
     */
    @Operation(summary = "获取上传文件详情", description = "根据文件ID获取上传文件的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "无权访问此文件"),
        @ApiResponse(responseCode = "404", description = "文件不存在")
    })
    @GetMapping("/{id}")
    public Result<UserUploadDTO> getUploadDetail(
            @Parameter(description = "文件ID", required = true) @PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new UserNotLoggedInException("未授权，请先登录");
        }
        
        UserUpload upload = uploadService.getFileById(id);
        
        // 验证文件所有权
        if (!upload.getUserId().equals(currentUser.getId())) {
            throw new AccessDeniedException("无权访问此文件");
        }
        
        return Result.success(UserUploadDTO.fromEntity(upload));
    }
    
    /**
     * 删除上传文件
     * @param id 文件ID
     * @return 操作结果
     */
    /**
     * 删除上传文件
     * @param id 文件ID
     * @return 操作结果
     */
    @Operation(summary = "删除上传文件", description = "根据文件ID删除上传文件")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "无权删除此文件"),
        @ApiResponse(responseCode = "404", description = "文件不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> deleteUpload(
            @Parameter(description = "文件ID", required = true) @PathVariable Long id) throws IOException {
        uploadService.deleteFile(id);
        return Result.success(null);
    }
    
    /**
     * 更新文件信息
     * @param id 文件ID
     * @param category 分类
     * @param description 描述
     * @param isPrivate 是否私有
     * @return 更新后的文件信息
     */
    /**
     * 更新文件信息
     * @param id 文件ID
     * @param category 分类
     * @param description 描述
     * @param isPrivate 是否私有
     * @return 更新后的文件信息
     */
    @Operation(summary = "更新文件信息", description = "根据文件ID更新上传文件的信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "无权修改此文件"),
        @ApiResponse(responseCode = "404", description = "文件不存在")
    })
    @PutMapping("/{id}")
    public Result<UserUploadDTO> updateUpload(
            @Parameter(description = "文件ID", required = true) @PathVariable Long id,
            @Parameter(description = "文件分类") @RequestParam(required = false) String category,
            @Parameter(description = "文件描述") @RequestParam(required = false) String description,
            @Parameter(description = "是否私有") @RequestParam(required = false) Boolean isPrivate) {
        UserUpload upload = uploadService.updateFileInfo(id, category, description, isPrivate);
        return Result.success(UserUploadDTO.fromEntity(upload));
    }
}
package com.thfh.controller;

import com.thfh.dto.DisabilityCertificationDTO;
import com.thfh.model.DisabilityCertification;
import com.thfh.service.DisabilityCertificationService;
import com.thfh.common.Result;
import com.thfh.util.ServerUrlUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 残疾人认证控制器
 * 提供残疾人认证申请、审核、查询等功能
 */
@Api(tags = "残疾人认证管理", description = "提供残疾人认证申请、审核、查询等功能")
@RestController
@RequestMapping("/api/disability-certification")
public class DisabilityCertificationController {

    @Autowired
    private DisabilityCertificationService certificationService;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private ServerUrlUtil serverUrlUtil;

    /**
     * 提交残疾人认证申请（支持文件上传）
     */
    @ApiOperation(value = "提交认证申请", notes = "提交残疾人认证申请，支持上传证明材料")
    @ApiResponses({
        @ApiResponse(code = 200, message = "提交成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<DisabilityCertificationDTO> submitCertification(
            @ApiParam(value = "用户认证信息", hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @ApiParam(value = "认证申请数据", required = true) @RequestPart("data") @Valid DisabilityCertificationDTO request,
            @ApiParam(value = "认证证明图片", required = false) @RequestPart(value = "file", required = false) MultipartFile file) {
        
        try {
            // 如果有上传文件，处理文件上传
            if (file != null && !file.isEmpty()) {
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

                // 设置文件URL
                String fileUrl = serverUrlUtil.getFileUrl(filename);
                request.setCertificateImage(fileUrl);
            } else if (request.getCertificateFile() != null && !request.getCertificateFile().isEmpty()) {
                // 如果通过DTO中的certificateFile上传文件
                MultipartFile certificateFile = request.getCertificateFile();
                
                // 创建上传目录（如果不存在）
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 生成唯一的文件名
                String originalFilename = certificateFile.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String filename = UUID.randomUUID().toString() + extension;

                // 保存文件
                Path filePath = uploadPath.resolve(filename);
                Files.copy(certificateFile.getInputStream(), filePath);

                // 设置文件URL
                String fileUrl = serverUrlUtil.getFileUrl(filename);
                request.setCertificateImage(fileUrl);
            }
            
            Long userId = Long.parseLong(userDetails.getUsername());
            DisabilityCertificationDTO certification = certificationService.submitCertification(userId, request);
            return Result.success(certification);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户的认证详情
     */
    @ApiOperation(value = "获取当前用户认证详情", notes = "获取当前登录用户的认证申请详情")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "认证记录不存在")
    })
    @GetMapping("/my")
    public Result<DisabilityCertificationDTO> getUserCertification(
            @ApiParam(value = "用户认证信息", hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        DisabilityCertificationDTO certification = certificationService.getUserCertification(userId);
        return Result.success(certification);
    }

    /**
     * 审核认证申请（管理员权限）
     */
    @ApiOperation(value = "审核认证申请", notes = "管理员审核用户提交的认证申请，可通过或驳回")
    @ApiResponses({
        @ApiResponse(code = 200, message = "审核成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有审核权限"),
        @ApiResponse(code = 404, message = "认证记录不存在")
    })
    @PutMapping("/{id}/review")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<DisabilityCertificationDTO> reviewCertification(
            @ApiParam(value = "认证记录ID", required = true) @PathVariable Long id,
            @ApiParam(value = "审核状态", required = true, example = "APPROVED") @RequestParam DisabilityCertification.Status status,
            @ApiParam(value = "驳回原因", required = false) @RequestParam(required = false) String rejectReason) {
        DisabilityCertificationDTO certification = certificationService.reviewCertification(id, status, rejectReason);
        return Result.success(certification);
    }

    /**
     * 分页查询认证列表（管理员权限）
     */
    @ApiOperation(value = "分页查询认证列表", notes = "管理员分页查询认证申请列表，支持多条件筛选")
    @ApiResponses({
        @ApiResponse(code = 200, message = "查询成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有查询权限")
    })
    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Page<DisabilityCertificationDTO>> getCertifications(
            @ApiParam(value = "用户名", required = false) @RequestParam(required = false) String username,
            @ApiParam(value = "真实姓名", required = false) @RequestParam(required = false) String realName,
            @ApiParam(value = "认证状态", required = false, example = "PENDING") @RequestParam(required = false) DisabilityCertification.Status status,
            @ApiParam(value = "分页参数", required = false) @PageableDefault(size = 10) Pageable pageable) {
        
        Specification<DisabilityCertification> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (username != null && !username.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        root.get("user").get("username"), "%" + username + "%"));
            }
            
            if (realName != null && !realName.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        root.get("user").get("realName"), "%" + realName + "%"));
            }
            
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<DisabilityCertificationDTO> certifications = certificationService.getCertifications(spec, pageable);
        return Result.success(certifications);
    }

    /**
     * 根据状态查询认证列表（管理员权限）
     */
    @ApiOperation(value = "根据状态查询认证列表", notes = "管理员根据状态查询认证申请列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "查询成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有查询权限")
    })
    @GetMapping("/by-status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<List<DisabilityCertificationDTO>> getCertificationsByStatus(
            @ApiParam(value = "认证状态", required = true, example = "PENDING") @RequestParam DisabilityCertification.Status status) {
        List<DisabilityCertificationDTO> certifications = certificationService.getCertificationsByStatus(status);
        return Result.success(certifications);
    }
} 
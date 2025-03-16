package com.thfh.controller;

import com.thfh.dto.DisabilityCertificationDTO;
import com.thfh.model.DisabilityCertification;
import com.thfh.service.DisabilityCertificationService;
import com.thfh.common.Result;
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

@RestController
@RequestMapping("/api/disability-certification")
public class DisabilityCertificationController {

    @Autowired
    private DisabilityCertificationService certificationService;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${server.port}")
    private String serverPort;

    /**
     * 获取服务器URL
     */
    private String getServerUrl() {
        return "http://localhost:" + serverPort;
    }

    /**
     * 提交残疾人认证申请（支持文件上传）
     */
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<DisabilityCertificationDTO> submitCertification(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("data") @Valid DisabilityCertificationDTO request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        
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
                String fileUrl = getServerUrl() + "/uploads/" + filename;
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
                String fileUrl = getServerUrl() + "/uploads/" + filename;
                request.setCertificateImage(fileUrl);
            }
            
            Long userId = Long.parseLong(userDetails.getUsername());
            DisabilityCertificationDTO certification = certificationService.submitCertification(userId, request);
            return Result.success(certification);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户的认证详情
     */
    @GetMapping("/my")
    public Result<DisabilityCertificationDTO> getUserCertification(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        DisabilityCertificationDTO certification = certificationService.getUserCertification(userId);
        return Result.success(certification);
    }

    /**
     * 审核认证申请（管理员权限）
     */
    @PutMapping("/{id}/review")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<DisabilityCertificationDTO> reviewCertification(
            @PathVariable Long id,
            @RequestParam DisabilityCertification.Status status,
            @RequestParam(required = false) String rejectReason) {
        DisabilityCertificationDTO certification = certificationService.reviewCertification(id, status, rejectReason);
        return Result.success(certification);
    }

    /**
     * 分页查询认证列表（管理员权限）
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Page<DisabilityCertificationDTO>> getCertifications(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) DisabilityCertification.Status status,
            @PageableDefault(size = 10) Pageable pageable) {
        
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
    @GetMapping("/by-status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<List<DisabilityCertificationDTO>> getCertificationsByStatus(
            @RequestParam DisabilityCertification.Status status) {
        List<DisabilityCertificationDTO> certifications = certificationService.getCertificationsByStatus(status);
        return Result.success(certifications);
    }
} 
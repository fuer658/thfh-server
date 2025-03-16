package com.thfh.dto;

import com.thfh.model.DisabilityCertification;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class DisabilityCertificationDTO {
    private Long id;
    private Long userId;
    private String username;
    private String realName;
    
    @NotBlank(message = "残疾证号码不能为空")
    private String certificateNumber;
    
    @NotBlank(message = "残疾类型不能为空")
    private String disabilityType;
    
    @NotBlank(message = "残疾等级不能为空")
    private String disabilityLevel;
    
    @NotBlank(message = "发证机构不能为空")
    private String issueAuthority;
    
    @NotNull(message = "发证日期不能为空")
    private LocalDateTime issueDate;
    
    @NotNull(message = "有效期不能为空")
    private LocalDateTime validUntil;
    
    private String certificateImage;
    
    private DisabilityCertification.Status status;
    private String statusDescription;
    private String rejectReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 仅用于请求时使用，不会持久化到数据库
    private transient MultipartFile certificateFile;
} 
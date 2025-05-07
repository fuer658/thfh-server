package com.thfh.dto;

import com.thfh.model.DisabilityCertification;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 残疾证认证数据传输对象
 * 用于残疾证认证相关操作
 */
@Data
@ApiModel(value = "残疾证认证信息", description = "包含残疾证认证的详细信息")
public class DisabilityCertificationDTO {
    @ApiModelProperty(value = "认证ID", notes = "唯一标识", example = "1")
    private Long id;
    
    @ApiModelProperty(value = "用户ID", example = "100")
    private Long userId;
    
    @ApiModelProperty(value = "用户名", example = "user123")
    private String username;
    
    @ApiModelProperty(value = "真实姓名", example = "张三")
    private String realName;
    
    @NotBlank(message = "残疾证号码不能为空")
    @ApiModelProperty(value = "残疾证号码", required = true, example = "12345678901234567X")
    private String certificateNumber;
    
    @NotBlank(message = "残疾类型不能为空")
    @ApiModelProperty(value = "残疾类型", required = true, example = "听力障碍")
    private String disabilityType;
    
    @NotBlank(message = "残疾等级不能为空")
    @ApiModelProperty(value = "残疾等级", required = true, example = "一级")
    private String disabilityLevel;
    
    @NotBlank(message = "发证机构不能为空")
    @ApiModelProperty(value = "发证机构", required = true, example = "北京市残疾人联合会")
    private String issueAuthority;
    
    @NotNull(message = "发证日期不能为空")
    @ApiModelProperty(value = "发证日期", required = true, example = "2020-01-01 00:00:00")
    private LocalDateTime issueDate;
    
    @NotNull(message = "有效期不能为空")
    @ApiModelProperty(value = "有效期", required = true, example = "2030-01-01 00:00:00")
    private LocalDateTime validUntil;
    
    @ApiModelProperty(value = "证书图片URL", example = "https://example.com/certificate.jpg")
    private String certificateImage;
    
    @ApiModelProperty(value = "认证状态", example = "PENDING")
    private DisabilityCertification.Status status;
    
    @ApiModelProperty(value = "状态描述", example = "等待审核")
    private String statusDescription;
    
    @ApiModelProperty(value = "拒绝原因", example = "证件信息与实名信息不符")
    private String rejectReason;
    
    @ApiModelProperty(value = "创建时间", example = "2023-01-01 10:00:00")
    private LocalDateTime createTime;
    
    @ApiModelProperty(value = "更新时间", example = "2023-01-02 15:30:00")
    private LocalDateTime updateTime;
    
    // 仅用于请求时使用，不会持久化到数据库
    @ApiModelProperty(value = "证书文件", notes = "上传证书时使用，不会持久化到数据库")
    private transient MultipartFile certificateFile;
} 
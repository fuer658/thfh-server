package com.thfh.model;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Entity
@Table(name = "company_details")
@Schema(description = "企业详情信息")
public class CompanyDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "企业详情ID", example = "1")
    private Long id;

    @Schema(description = "企业名称", example = "ABC公司")
    private String name;

    @Schema(description = "企业地址", example = "北京市海淀区")
    private String address;

    @Schema(description = "企业联系方式", example = "010-12345678")
    private String contact;

    @Schema(description = "企业简介", example = "这是一家专注于软件开发的公司")
    private String description;

    @Schema(description = "企业logo URL", example = "https://example.com/logo.png")
    private String logoUrl;

    @Schema(description = "企业官网", example = "https://www.example.com")
    private String website;

    @Schema(description = "企业规模", example = "100-500人")
    private String scale;

    @Schema(description = "企业行业", example = "互联网")
    private String industry;

    @Schema(description = "企业成立时间", example = "2010-01-01")
    private String establishmentDate;

    @Schema(description = "企业认证状态", example = "VERIFIED")
    private String verificationStatus;
}
package com.thfh.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 公司详情DTO
 */
@Data
@Getter
@Setter
@Schema(description = "公司详情DTO - 公司详细信息")
public class CompanyDetails {
    
    @Schema(description = "公司ID", example = "1")
    private Long id;
    
    @Schema(description = "公司名称", example = "示例科技有限公司")
    private String name;
    
    @Schema(description = "公司简介", example = "一家专注于无障碍技术的科技公司")
    private String description;
    
    @Schema(description = "公司地址", example = "北京市海淀区中关村大街1号")
    private String address;
    
    @Schema(description = "公司电话", example = "010-12345678")
    private String phone;
    
    @Schema(description = "公司邮箱", example = "contact@example.com")
    private String email;
    
    @Schema(description = "公司网站 - 公司官方网站URL", example = "https://www.example.com")
    private String website;
    
    @Schema(description = "公司LOGO", example = "https://example.com/logo.png")
    private String logo;
    
    @Schema(description = "公司规模", example = "100-499人")
    private String scale;
    
    @Schema(description = "成立时间", example = "2010-01-01")
    private String foundingTime;
    
    @Schema(description = "公司类型", example = "科技公司")
    private String type;
    
    @Schema(description = "公司标签", example = "人工智能,无障碍技术,互联网")
    private String tags;
    
    @Schema(description = "公司福利", example = "五险一金,带薪年假,免费班车")
    private String benefits;
    
    @Schema(description = "公司相册", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
    private List<String> photos;
    
    @Schema(description = "公司视频", example = "https://example.com/video.mp4")
    private String video;
    
    @Schema(description = "公司认证状态", example = "已认证")
    private String verificationStatus;
    
    @Schema(description = "公司评分", example = "4.5")
    private Float rating;
    
    @Schema(description = "评论数量", example = "120")
    private Integer reviewCount;
    
    @Schema(description = "在招职位数", example = "15")
    private Integer jobCount;
    
    @Schema(description = "关注人数", example = "500")
    private Integer followerCount;
    
    @Schema(description = "当前用户是否已关注", example = "true")
    private Boolean isFollowed;
}

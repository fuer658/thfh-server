package com.thfh.dto;

import com.thfh.model.JobStatus;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 工作岗位数据传输对象
 * 用于在不同层之间传输工作岗位信息
 */
@Data
@Schema(description = "工作岗位信息 - 包含工作岗位的详细信息")
public class JobDTO {
    /**
     * 工作岗位ID，唯一标识
     */
    @Schema(description = "岗位ID", description = "唯一标识", example = "1")
    private Long id;
    
    /**
     * 工作岗位标题
     */
    @Schema(description = "岗位标题", required = true, example = "软件工程师")
    private String title;
    
    /**
     * 工作岗位详细描述
     */
    @Schema(description = "岗位描述", example = "负责公司产品的开发和维护")
    private String description;
    
    /**
     * 发布公司ID
     */
    @Schema(description = "公司ID", example = "100")
    private Long companyId;
    
    /**
     * 发布公司名称
     */
    @Schema(description = "公司名称", example = "某某科技有限公司")
    private String companyName;
    
    /**
     * 职位分类ID
     */
    @Schema(description = "分类ID", example = "5")
    private Long categoryId;
    
    /**
     * 职位分类名称
     */
    @Schema(description = "分类名称", example = "技术开发")
    private String categoryName;
    
    /**
     * 工作地点
     */
    @Schema(description = "工作地点", example = "北京市海淀区")
    private String location;
    
    /**
     * 薪资范围下限
     */
    @Schema(description = "薪资下限", example = "10000")
    private BigDecimal salaryMin;
    
    /**
     * 薪资范围上限
     */
    @Schema(description = "薪资上限", example = "20000")
    private BigDecimal salaryMax;
    
    /**
     * 岗位要求
     */
    @Schema(description = "岗位要求", example = "熟悉Java编程，有2年以上工作经验")
    private String requirements;
    
    /**
     * 工作福利
     */
    @Schema(description = "工作福利", example = "五险一金，节日福利")
    private String benefits;
    
    /**
     * 残障人士支持措施
     */
    @Schema(description = "残障支持措施", example = "配备轮椅通道，提供辅助设备")
    private String disabilitySupport;
    
    /**
     * 联系人姓名
     */
    @Schema(description = "联系人姓名", example = "李先生")
    private String contactPerson;
    
    /**
     * 联系人电话
     */
    @Schema(description = "联系人电话", example = "13900001234")
    private String contactPhone;
    
    /**
     * 联系人邮箱
     */
    @Schema(description = "联系人邮箱", example = "hr@example.com")
    private String contactEmail;
    
    /**
     * 公司待遇（从Company移动过来）
     */
    @Schema(description = "公司待遇", example = "年终奖金，绩效奖金")
    private String companyTreatment;
    
    /**
     * 员工福利（从Company移动过来）
     */
    @Schema(description = "员工福利", example = "团队建设，生日礼品")
    private String employeeBenefits;
    
    /**
     * 晋升前景（从Company移动过来）
     */
    @Schema(description = "晋升前景", example = "定期绩效评估，晋升机会")
    private String promotionProspects;
    
    /**
     * 岗位详细要求（从Company移动过来）
     */
    @Schema(description = "岗位详细要求", example = "需具备良好的团队合作精神")
    private String jobRequirements;
    
    /**
     * 招聘岗位，多个岗位以逗号分隔（从Company移动过来）
     */
    @Schema(description = "招聘岗位", example = "前端开发工程师,后端开发工程师")
    private String positions;
    
    /**
     * 职位标签，多个标签以逗号分隔，用于前端展示
     */
    @Schema(description = "职位标签", example = "五险一金,年终奖,弹性工作")
    private String tags;
    
    /**
     * 岗位状态（如：草稿、已发布、已关闭等）
     */
    @Schema(description = "岗位状态", description = "如草稿、已发布、已关闭等", example = "PUBLISHED")
    private JobStatus status;
    
    /**
     * 岗位浏览次数
     */
    @Schema(description = "浏览次数", example = "1024")
    private Integer viewCount;
    
    /**
     * 岗位申请次数
     */
    @Schema(description = "申请次数", example = "56")
    private Integer applyCount;
    
    /**
     * 岗位是否启用
     */
    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;
    
    /**
     * 岗位创建时间
     */
    @Schema(description = "创建时间", example = "2023-01-01 10:00:00")
    private LocalDateTime createTime;
} 
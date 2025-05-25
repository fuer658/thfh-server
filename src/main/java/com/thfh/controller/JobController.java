package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.JobDTO;
import com.thfh.dto.JobQueryDTO;
import com.thfh.dto.UserDTO;
import com.thfh.exception.ResourceNotFoundException;
import com.thfh.model.Job;
import com.thfh.model.User;
import com.thfh.service.JobService;
import com.thfh.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工作/职位管理控制器
 * 提供工作/职位的增删改查、发布、关闭和状态切换等功能
 */
@Tag(name = "职位管理", description = "提供工作/职位的增删改查、发布、关闭和状态切换等功能")
@RestController
@RequestMapping("/api/jobs")
public class JobController {
    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    /**
     * 获取工作/职位列表
     * @param queryDTO 查询条件，包含公司ID、职位类型和分页信息等
     * @return 工作/职位分页列表
     */
    @Operation(summary = "获取职位列表", description = "根据查询条件获取职位分页列表，支持多种筛选条件")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping
    public Result<Page<JobDTO>> getJobs(
            @Parameter(description = "查询条件，包含公司ID、职位类型和分页信息等") JobQueryDTO queryDTO) {
        return Result.success(jobService.getJobs(queryDTO));
    }

    /**
     * 创建新工作/职位
     * @param jobDTO 工作/职位信息
     * @return 创建的工作/职位信息
     */
    @Operation(summary = "创建新职位", description = "创建一个新的职位，需要提供职位的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @PostMapping
    public Result<JobDTO> createJob(
            @Parameter(description = "职位信息", required = true) @RequestBody @Validated JobDTO jobDTO) {
        // 进行数据验证
        validateJobData(jobDTO);
        
        // 确保enabled字段有值，默认为true
        if (jobDTO.getEnabled() == null) {
            jobDTO.setEnabled(true);
        }
        
        // 调用服务创建职位
        JobDTO createdJob = jobService.createJob(jobDTO);
        return Result.success(createdJob, "职位创建成功");
    }

    /**
     * 更新工作/职位信息
     * @param id 工作/职位ID
     * @param jobDTO 更新的工作/职位信息
     * @return 更新后的工作/职位信息
     */
    @Operation(summary = "更新职位信息", description = "根据职位ID更新职位信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "职位不存在")
    })
    @PutMapping("/{id}")
    public Result<JobDTO> updateJob(
            @Parameter(description = "职位ID", required = true) @PathVariable Long id,
            @Parameter(description = "更新的职位信息", required = true) @RequestBody @Validated JobDTO jobDTO) {
        // 进行数据验证
        validateJobData(jobDTO);
        
        return Result.success(jobService.updateJob(id, jobDTO), "职位更新成功");
    }

    /**
     * 发布工作/职位
     * @param id 工作/职位ID
     * @return 操作结果
     */
    @Operation(summary = "发布职位", description = "将指定职位的状态更改为已发布")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "发布成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "职位不存在")
    })
    @PutMapping("/{id}/publish")
    public Result<Void> publishJob(
            @Parameter(description = "职位ID", required = true) @PathVariable Long id) {
        jobService.publishJob(id);
        return Result.success(null, "职位发布成功");
    }

    /**
     * 关闭工作/职位
     * @param id 工作/职位ID
     * @return 操作结果
     */
    @Operation(summary = "关闭职位", description = "将指定职位的状态更改为已关闭")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "关闭成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "职位不存在")
    })
    @PutMapping("/{id}/close")
    public Result<Void> closeJob(
            @Parameter(description = "职位ID", required = true) @PathVariable Long id) {
        jobService.closeJob(id);
        return Result.success(null, "职位已关闭");
    }

    /**
     * 删除工作/职位
     * @param id 工作/职位ID
     * @return 操作结果
     */
    @Operation(summary = "删除职位", description = "根据职位ID删除职位")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限删除该职位"),
        @ApiResponse(responseCode = "404", description = "职位不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> deleteJob(
            @Parameter(description = "职位ID", required = true) @PathVariable Long id) {
        jobService.deleteJob(id);
        return Result.success(null, "职位删除成功");
    }

    /**
     * 切换工作/职位状态（启用/禁用）
     * @param id 工作/职位ID
     * @return 操作结果
     */
    @Operation(summary = "切换职位状态", description = "启用或禁用职位")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "操作成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "职位不存在")
    })
    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleJobStatus(
            @Parameter(description = "职位ID", required = true) @PathVariable Long id) {
        jobService.toggleJobStatus(id);
        return Result.success(null, "职位状态切换成功");
    }

    /**
     * 获取职位的申请数量统计
     * @param id 职位ID
     * @return 包含各状态申请数量的统计数据
     */
    @Operation(summary = "获取职位申请数量统计", description = "获取指定职位的各状态申请数量统计")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "职位不存在")
    })
    @GetMapping("/{id}/application-counts")
    public Result<Map<String, Long>> getJobApplicationCounts(
            @Parameter(description = "职位ID", required = true) @PathVariable Long id) {
        return Result.success(jobService.getJobApplicationCounts(id));
    }

    /**
     * 获取职位关联的企业用户
     * 
     * @param jobId 职位ID
     * @return 该职位关联的企业用户列表
     */
    @Operation(summary = "获取职位关联的企业用户", description = "通过职位ID获取该职位所属公司的企业用户列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "404", description = "职位不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/{jobId}/company-users")
    public Result<List<UserDTO>> getJobCompanyUsers(
            @Parameter(description = "职位ID", required = true) @PathVariable Long jobId) {
        // 获取职位信息
        Job job = jobService.getJobById(jobId);
        if (job == null) {
            throw new ResourceNotFoundException("职位不存在");
        }
        
        // 获取公司ID
        Long companyId = job.getCompany().getId();
        
        // 获取企业用户列表
        List<User> users = userService.findUsersByCompanyId(companyId);
        List<UserDTO> userDTOs = users.stream()
                .map(user -> userService.convertToDTO(user))
                .collect(Collectors.toList());
                    
        return Result.success(userDTOs);
    }
    
    /**
     * 验证职位数据有效性
     * @param jobDTO 待验证的职位数据
     * @throws IllegalArgumentException 当数据验证失败时抛出
     */
    private void validateJobData(JobDTO jobDTO) {
        // 验证必填字段
        if (jobDTO.getTitle() == null || jobDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("职位名称不能为空");
        }
        
        if (jobDTO.getCompanyId() == null) {
            throw new IllegalArgumentException("必须指定所属公司");
        }
        
        if (jobDTO.getLocation() == null || jobDTO.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("工作地点不能为空");
        }
        
        // 验证薪资范围
        if (jobDTO.getSalaryMin() != null && jobDTO.getSalaryMax() != null) {
            if (jobDTO.getSalaryMin().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("最低薪资不能为负数");
            }
            
            if (jobDTO.getSalaryMax().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("最高薪资不能为负数");
            }
            
            if (jobDTO.getSalaryMin().compareTo(jobDTO.getSalaryMax()) > 0) {
                throw new IllegalArgumentException("最低薪资不能大于最高薪资");
            }
        }
        
        // 验证字段长度
        if (jobDTO.getTitle() != null && jobDTO.getTitle().length() > 50) {
            throw new IllegalArgumentException("职位名称长度不能超过50个字符");
        }
        
        if (jobDTO.getDescription() != null && jobDTO.getDescription().length() > 2000) {
            throw new IllegalArgumentException("职位描述长度不能超过2000个字符");
        }
        
        if (jobDTO.getRequirements() != null && jobDTO.getRequirements().length() > 1000) {
            throw new IllegalArgumentException("任职要求长度不能超过1000个字符");
        }
    }
} 
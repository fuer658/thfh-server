package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.JobDTO;
import com.thfh.dto.JobQueryDTO;
import com.thfh.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 工作/职位管理控制器
 * 提供工作/职位的增删改查、发布、关闭和状态切换等功能
 */
@RestController
@RequestMapping("/api/jobs")
public class JobController {
    @Autowired
    private JobService jobService;

    /**
     * 获取工作/职位列表
     * @param queryDTO 查询条件，包含公司ID、职位类型和分页信息等
     * @return 工作/职位分页列表
     */
    @GetMapping
    public Result<Page<JobDTO>> getJobs(JobQueryDTO queryDTO) {
        return Result.success(jobService.getJobs(queryDTO));
    }

    /**
     * 创建新工作/职位
     * @param jobDTO 工作/职位信息
     * @return 创建的工作/职位信息
     */
    @PostMapping
    public Result<JobDTO> createJob(@RequestBody @Validated JobDTO jobDTO) {
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
    @PutMapping("/{id}")
    public Result<JobDTO> updateJob(@PathVariable Long id, @RequestBody @Validated JobDTO jobDTO) {
        // 进行数据验证
        validateJobData(jobDTO);
        
        return Result.success(jobService.updateJob(id, jobDTO), "职位更新成功");
    }

    /**
     * 发布工作/职位
     * @param id 工作/职位ID
     * @return 操作结果
     */
    @PutMapping("/{id}/publish")
    public Result<Void> publishJob(@PathVariable Long id) {
        jobService.publishJob(id);
        return Result.success(null, "职位发布成功");
    }

    /**
     * 关闭工作/职位
     * @param id 工作/职位ID
     * @return 操作结果
     */
    @PutMapping("/{id}/close")
    public Result<Void> closeJob(@PathVariable Long id) {
        jobService.closeJob(id);
        return Result.success(null, "职位已关闭");
    }

    /**
     * 删除工作/职位
     * @param id 工作/职位ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return Result.success(null, "职位删除成功");
    }

    /**
     * 切换工作/职位状态（启用/禁用）
     * @param id 工作/职位ID
     * @return 操作结果
     */
    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleJobStatus(@PathVariable Long id) {
        jobService.toggleJobStatus(id);
        return Result.success(null, "职位状态切换成功");
    }

    /**
     * 获取职位的申请数量统计
     * @param id 职位ID
     * @return 包含各状态申请数量的统计数据
     */
    @GetMapping("/{id}/application-counts")
    public Result<Map<String, Long>> getJobApplicationCounts(@PathVariable Long id) {
        return Result.success(jobService.getJobApplicationCounts(id));
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
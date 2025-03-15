package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.JobDTO;
import com.thfh.dto.JobQueryDTO;
import com.thfh.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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
    public Result<JobDTO> createJob(@RequestBody JobDTO jobDTO) {
        return Result.success(jobService.createJob(jobDTO));
    }

    /**
     * 更新工作/职位信息
     * @param id 工作/职位ID
     * @param jobDTO 更新的工作/职位信息
     * @return 更新后的工作/职位信息
     */
    @PutMapping("/{id}")
    public Result<JobDTO> updateJob(@PathVariable Long id, @RequestBody JobDTO jobDTO) {
        return Result.success(jobService.updateJob(id, jobDTO));
    }

    /**
     * 发布工作/职位
     * @param id 工作/职位ID
     * @return 操作结果
     */
    @PutMapping("/{id}/publish")
    public Result<Void> publishJob(@PathVariable Long id) {
        jobService.publishJob(id);
        return Result.success(null);
    }

    /**
     * 关闭工作/职位
     * @param id 工作/职位ID
     * @return 操作结果
     */
    @PutMapping("/{id}/close")
    public Result<Void> closeJob(@PathVariable Long id) {
        jobService.closeJob(id);
        return Result.success(null);
    }

    /**
     * 删除工作/职位
     * @param id 工作/职位ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return Result.success(null);
    }

    /**
     * 切换工作/职位状态（启用/禁用）
     * @param id 工作/职位ID
     * @return 操作结果
     */
    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleJobStatus(@PathVariable Long id) {
        jobService.toggleJobStatus(id);
        return Result.success(null);
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
} 
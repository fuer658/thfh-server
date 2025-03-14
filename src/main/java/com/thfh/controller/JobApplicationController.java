package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.JobApplicationDTO;
import com.thfh.dto.JobApplicationQueryDTO;
import com.thfh.model.JobApplicationStatus;
import com.thfh.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 职位申请控制器
 * 提供职位申请相关的API接口
 */
@RestController
@RequestMapping("/api/job-applications")
public class JobApplicationController {
    @Autowired
    private JobApplicationService jobApplicationService;

    /**
     * 创建职位申请
     * @param jobApplicationDTO 职位申请信息
     * @return 创建后的职位申请信息
     */
    @PostMapping
    public Result<JobApplicationDTO> createJobApplication(@RequestBody JobApplicationDTO jobApplicationDTO) {
        return Result.success(jobApplicationService.createJobApplication(jobApplicationDTO));
    }

    /**
     * 获取职位申请列表
     * @param queryDTO 查询条件
     * @return 分页的职位申请列表
     */
    @GetMapping
    public Result<Page<JobApplicationDTO>> getJobApplications(JobApplicationQueryDTO queryDTO) {
        return Result.success(jobApplicationService.getJobApplications(queryDTO));
    }

    /**
     * 获取职位申请详情
     * @param id 职位申请ID
     * @return 职位申请详情
     */
    @GetMapping("/{id}")
    public Result<JobApplicationDTO> getJobApplicationById(@PathVariable Long id) {
        return Result.success(jobApplicationService.getJobApplicationById(id));
    }

    /**
     * 更新职位申请状态
     * @param id 职位申请ID
     * @param status 新状态
     * @param reason 状态变更原因（如拒绝原因）
     * @return 更新后的职位申请信息
     */
    @PutMapping("/{id}/status")
    public Result<JobApplicationDTO> updateJobApplicationStatus(
            @PathVariable Long id,
            @RequestParam JobApplicationStatus status,
            @RequestParam(required = false) String reason) {
        return Result.success(jobApplicationService.updateJobApplicationStatus(id, status, reason));
    }

    /**
     * 安排面试
     * @param id 职位申请ID
     * @param interviewTime 面试时间
     * @param interviewLocation 面试地点
     * @param notes 面试备注
     * @return 更新后的职位申请信息
     */
    @PutMapping("/{id}/interview")
    public Result<JobApplicationDTO> arrangeInterview(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime interviewTime,
            @RequestParam String interviewLocation,
            @RequestParam(required = false) String notes) {
        return Result.success(jobApplicationService.arrangeInterview(id, interviewTime, interviewLocation, notes));
    }

    /**
     * 标记申请为已读
     * @param id 职位申请ID
     * @return 更新后的职位申请信息
     */
    @PutMapping("/{id}/read")
    public Result<JobApplicationDTO> markAsRead(@PathVariable Long id) {
        return Result.success(jobApplicationService.markAsRead(id));
    }

    /**
     * 撤回职位申请
     * @param id 职位申请ID
     * @param userId 用户ID（用于验证是否是申请人）
     * @return 更新后的职位申请信息
     */
    @PutMapping("/{id}/withdraw")
    public Result<JobApplicationDTO> withdrawApplication(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return Result.success(jobApplicationService.withdrawApplication(id, userId));
    }

    /**
     * 删除职位申请
     * @param id 职位申请ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteJobApplication(@PathVariable Long id) {
        jobApplicationService.deleteJobApplication(id);
        return Result.success(null);
    }

    /**
     * 更新申请备注
     * @param id 职位申请ID
     * @param notes 备注内容
     * @return 更新后的职位申请信息
     */
    @PutMapping("/{id}/notes")
    public Result<JobApplicationDTO> updateNotes(
            @PathVariable Long id,
            @RequestParam String notes) {
        return Result.success(jobApplicationService.updateNotes(id, notes));
    }

    /**
     * 获取职位未读申请数量
     * @param jobId 职位ID
     * @return 未读申请数量
     */
    @GetMapping("/unread/job/{jobId}")
    public Result<Long> getUnreadApplicationCount(@PathVariable Long jobId) {
        return Result.success(jobApplicationService.getUnreadApplicationCount(jobId));
    }

    /**
     * 获取公司未读申请数量
     * @param companyId 公司ID
     * @return 未读申请数量
     */
    @GetMapping("/unread/company/{companyId}")
    public Result<Long> getCompanyUnreadApplicationCount(@PathVariable Long companyId) {
        return Result.success(jobApplicationService.getCompanyUnreadApplicationCount(companyId));
    }
} 
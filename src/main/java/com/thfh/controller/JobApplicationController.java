package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.JobApplicationDTO;
import com.thfh.dto.JobApplicationQueryDTO;
import com.thfh.model.JobApplicationStatus;
import com.thfh.service.JobApplicationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 职位申请控制器
 * 提供职位申请相关的API接口
 */
@Tag(name = "职位申请管理", description = "提供职位申请的增删改查、状态管理和面试安排等功能")
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
    @Operation(summary = "创建职位申请", description = "用户申请职位，创建一条申请记录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "申请成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "409", description = "已申请过该职位")
    })
    @PostMapping
    public Result<JobApplicationDTO> createJobApplication(
            @Parameter(description = "职位申请信息", required = true) @RequestBody JobApplicationDTO jobApplicationDTO) {
        return Result.success(jobApplicationService.createJobApplication(jobApplicationDTO));
    }

    /**
     * 获取职位申请列表
     * @param queryDTO 查询条件
     * @return 分页的职位申请列表
     */
    @Operation(summary = "获取职位申请列表", description = "根据查询条件获取职位申请列表，支持多种筛选条件")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping
    public Result<Page<JobApplicationDTO>> getJobApplications(
            @Parameter(description = "查询条件，包含职位ID、用户ID和分页信息等") JobApplicationQueryDTO queryDTO) {
        return Result.success(jobApplicationService.getJobApplications(queryDTO));
    }

    /**
     * 获取职位申请详情
     * @param id 职位申请ID
     * @return 职位申请详情
     */
    @Operation(summary = "获取职位申请详情", description = "根据申请ID获取职位申请的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "申请不存在")
    })
    @GetMapping("/{id}")
    public Result<JobApplicationDTO> getJobApplicationById(
            @Parameter(description = "申请ID", required = true) @PathVariable Long id) {
        return Result.success(jobApplicationService.getJobApplicationById(id));
    }

    /**
     * 更新职位申请状态
     * @param id 职位申请ID
     * @param status 新状态
     * @param reason 状态变更原因（如拒绝原因）
     * @return 更新后的职位申请信息
     */
    @Operation(summary = "更新职位申请状态", description = "更新职位申请的状态，如通过、拒绝等")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限更新该申请状态"),
        @ApiResponse(responseCode = "404", description = "申请不存在")
    })
    @PutMapping("/{id}/status")
    public Result<JobApplicationDTO> updateJobApplicationStatus(
            @Parameter(description = "申请ID", required = true) @PathVariable Long id,
            @Parameter(description = "新状态", required = true) @RequestParam JobApplicationStatus status,
            @Parameter(description = "状态变更原因", example = "不符合岗位要求") @RequestParam(required = false) String reason) {
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
    @Operation(summary = "安排面试", description = "为申请通过初筛的候选人安排面试")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "安排成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限安排面试"),
        @ApiResponse(responseCode = "404", description = "申请不存在")
    })
    @PutMapping("/{id}/interview")
    public Result<JobApplicationDTO> arrangeInterview(
            @Parameter(description = "申请ID", required = true) @PathVariable Long id,
            @Parameter(description = "面试时间", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime interviewTime,
            @Parameter(description = "面试地点", required = true) @RequestParam String interviewLocation,
            @Parameter(description = "面试备注", example = "请携带简历原件") @RequestParam(required = false) String notes) {
        return Result.success(jobApplicationService.arrangeInterview(id, interviewTime, interviewLocation, notes));
    }

    /**
     * 标记申请为已读
     * @param id 职位申请ID
     * @return 更新后的职位申请信息
     */
    @Operation(summary = "标记申请为已读", description = "将未读的申请标记为已读状态")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "标记成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限标记该申请"),
        @ApiResponse(responseCode = "404", description = "申请不存在")
    })
    @PutMapping("/{id}/read")
    public Result<JobApplicationDTO> markAsRead(
            @Parameter(description = "申请ID", required = true) @PathVariable Long id) {
        return Result.success(jobApplicationService.markAsRead(id));
    }

    /**
     * 撤回职位申请
     * @param id 职位申请ID
     * @param userId 用户ID（用于验证是否是申请人）
     * @return 更新后的职位申请信息
     */
    @Operation(summary = "撤回职位申请", description = "用户撤回已提交的职位申请")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "撤回成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "不是申请人，无法撤回"),
        @ApiResponse(responseCode = "404", description = "申请不存在"),
        @ApiResponse(responseCode = "409", description = "申请状态不允许撤回")
    })
    @PutMapping("/{id}/withdraw")
    public Result<JobApplicationDTO> withdrawApplication(
            @Parameter(description = "申请ID", required = true) @PathVariable Long id,
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        return Result.success(jobApplicationService.withdrawApplication(id, userId));
    }

    /**
     * 删除职位申请
     * @param id 职位申请ID
     * @return 操作结果
     */
    @Operation(summary = "删除职位申请", description = "根据申请ID删除职位申请")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限删除该申请"),
        @ApiResponse(responseCode = "404", description = "申请不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> deleteJobApplication(
            @Parameter(description = "申请ID", required = true) @PathVariable Long id) {
        jobApplicationService.deleteJobApplication(id);
        return Result.success(null);
    }

    /**
     * 更新申请备注
     * @param id 职位申请ID
     * @param notes 备注内容
     * @return 更新后的职位申请信息
     */
    @Operation(summary = "更新申请备注", description = "更新职位申请的备注信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限更新该申请备注"),
        @ApiResponse(responseCode = "404", description = "申请不存在")
    })
    @PutMapping("/{id}/notes")
    public Result<JobApplicationDTO> updateNotes(
            @Parameter(description = "申请ID", required = true) @PathVariable Long id,
            @Parameter(description = "备注内容", required = true) @RequestParam String notes) {
        return Result.success(jobApplicationService.updateNotes(id, notes));
    }

    /**
     * 获取职位未读申请数量
     * @param jobId 职位ID
     * @return 未读申请数量
     */
    @Operation(summary = "获取职位未读申请数量", description = "获取指定职位的未读申请数量")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/unread/job/{jobId}")
    public Result<Long> getUnreadApplicationCount(
            @Parameter(description = "职位ID", required = true) @PathVariable Long jobId) {
        return Result.success(jobApplicationService.getUnreadApplicationCount(jobId));
    }

    /**
     * 获取公司未读申请数量
     * @param companyId 公司ID
     * @return 未读申请数量
     */
    @Operation(summary = "获取公司未读申请数量", description = "获取指定公司的所有职位的未读申请总数")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/unread/company/{companyId}")
    public Result<Long> getCompanyUnreadApplicationCount(
            @Parameter(description = "公司ID", required = true) @PathVariable Long companyId) {
        return Result.success(jobApplicationService.getCompanyUnreadApplicationCount(companyId));
    }
} 
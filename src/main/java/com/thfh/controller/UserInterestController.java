package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.UserInterestDTO;
import com.thfh.model.InterestType;
import com.thfh.service.UserInterestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户兴趣控制器
 * 提供用户兴趣相关的API接口
 */
@Tag(name = "用户兴趣", description = "用户兴趣相关的API接口")
@RestController
@RequestMapping("/api/interests")
public class UserInterestController {
    @Autowired
    private UserInterestService userInterestService;

    /**
     * 获取所有可用的兴趣类型
     * @return 所有兴趣类型列表
     */
    @Operation(summary = "获取所有兴趣类型", description = "获取系统中所有可选的兴趣类型")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/types")
    public Result<List<InterestType>> getAllInterestTypes() {
        return Result.success(userInterestService.getAllInterestTypes());
    }

    /**
     * 获取当前登录用户的兴趣
     * @return 当前用户的兴趣
     */
    @Operation(summary = "获取当前用户兴趣", description = "获取当前登录用户的兴趣列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/my")
    public Result<UserInterestDTO> getCurrentUserInterests() {
        return Result.success(userInterestService.getCurrentUserInterests());
    }

    /**
     * 获取指定用户的兴趣
     * @param userId 用户ID
     * @return 指定用户的兴趣
     */
    @Operation(summary = "获取指定用户兴趣", description = "根据用户ID获取该用户的兴趣列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/user/{userId}")
    public Result<UserInterestDTO> getUserInterests(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        return Result.success(userInterestService.getUserInterests(userId));
    }

    /**
     * 更新用户兴趣
     * @param dto 用户兴趣DTO
     * @return 更新后的用户兴趣
     */
    @Operation(summary = "更新用户兴趣", description = "更新用户的兴趣列表，需要用户已登录且只能更新自己的兴趣")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限修改该用户的兴趣"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping
    public Result<UserInterestDTO> updateUserInterests(
            @Parameter(description = "用户兴趣信息", required = true) @RequestBody UserInterestDTO dto) {
        return Result.success(userInterestService.updateUserInterests(dto));
    }
} 
package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.UserInterestDTO;
import com.thfh.model.InterestType;
import com.thfh.service.UserInterestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户兴趣控制器
 * 提供用户兴趣相关的API接口
 */
@Api(tags = "用户兴趣", description = "用户兴趣相关的API接口")
@RestController
@RequestMapping("/api/interests")
public class UserInterestController {
    @Autowired
    private UserInterestService userInterestService;

    /**
     * 获取所有可用的兴趣类型
     * @return 所有兴趣类型列表
     */
    @ApiOperation(value = "获取所有兴趣类型", notes = "获取系统中所有可选的兴趣类型")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功")
    })
    @GetMapping("/types")
    public Result<List<InterestType>> getAllInterestTypes() {
        return Result.success(userInterestService.getAllInterestTypes());
    }

    /**
     * 获取当前登录用户的兴趣
     * @return 当前用户的兴趣
     */
    @ApiOperation(value = "获取当前用户兴趣", notes = "获取当前登录用户的兴趣列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
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
    @ApiOperation(value = "获取指定用户兴趣", notes = "根据用户ID获取该用户的兴趣列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @GetMapping("/user/{userId}")
    public Result<UserInterestDTO> getUserInterests(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        return Result.success(userInterestService.getUserInterests(userId));
    }

    /**
     * 更新用户兴趣
     * @param dto 用户兴趣DTO
     * @return 更新后的用户兴趣
     */
    @ApiOperation(value = "更新用户兴趣", notes = "更新用户的兴趣列表，需要用户已登录且只能更新自己的兴趣")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限修改该用户的兴趣"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping
    public Result<UserInterestDTO> updateUserInterests(
            @ApiParam(value = "用户兴趣信息", required = true) @RequestBody UserInterestDTO dto) {
        return Result.success(userInterestService.updateUserInterests(dto));
    }
} 
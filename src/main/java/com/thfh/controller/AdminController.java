package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.AdminDTO;
import com.thfh.service.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 * 提供管理员的增删改查等管理功能
 */
@Tag(name = "管理员管理", description = "管理员相关的API接口，包括管理员查询、创建、更新和删除等功能")
@RestController
@RequestMapping("/api/admins")
public class AdminController {
    @Autowired
    private AdminService adminService;

    /**
     * 获取管理员列表
     * @param queryDTO 查询条件，包含分页信息和筛选条件
     * @return 管理员分页列表
     */
    @Operation(summary = "获取管理员列表", description = "根据查询条件获取管理员分页列表，支持多种筛选条件")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping
    public Result<Page<AdminDTO>> getAdmins(
            @Parameter(description = "查询条件，包含分页信息和筛选条件") AdminDTO queryDTO) {
        return Result.success(adminService.getAdmins(queryDTO));
    }

    /**
     * 创建新管理员
     * @param adminDTO 管理员信息
     * @return 创建的管理员信息
     */
    @Operation(summary = "创建新管理员", description = "创建一个新的管理员账号，需要提供管理员的基本信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "409", description = "用户名已存在")
    })
    @PostMapping
    public Result<AdminDTO> createAdmin(
            @Parameter(description = "管理员信息", required = true) @RequestBody AdminDTO adminDTO) {
        return Result.success(adminService.createAdmin(adminDTO));
    }

    /**
     * 更新管理员信息
     * @param id 管理员ID
     * @param adminDTO 更新的管理员信息
     * @return 更新后的管理员信息
     */
    @Operation(summary = "更新管理员信息", description = "根据管理员ID更新管理员信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "管理员不存在"),
        @ApiResponse(responseCode = "409", description = "新用户名已存在")
    })
    @PutMapping("/{id}")
    public Result<AdminDTO> updateAdmin(
            @Parameter(description = "管理员ID", required = true) @PathVariable Long id,
            @Parameter(description = "更新的管理员信息", required = true) @RequestBody AdminDTO adminDTO) {
        return Result.success(adminService.updateAdmin(id, adminDTO));
    }

    /**
     * 删除管理员
     * @param id 管理员ID
     * @return 操作结果
     */
    @Operation(summary = "删除管理员", description = "根据管理员ID删除管理员")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "管理员不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> deleteAdmin(
            @Parameter(description = "管理员ID", required = true) @PathVariable Long id) {
        adminService.deleteAdmin(id);
        return Result.success(null);
    }

    /**
     * 切换管理员状态（启用/禁用）
     * @param id 管理员ID
     * @return 操作结果
     */
    @Operation(summary = "切换管理员状态", description = "启用或禁用管理员账号")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "操作成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "管理员不存在")
    })
    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleAdminStatus(
            @Parameter(description = "管理员ID", required = true) @PathVariable Long id) {
        adminService.toggleAdminStatus(id);
        return Result.success(null);
    }
}
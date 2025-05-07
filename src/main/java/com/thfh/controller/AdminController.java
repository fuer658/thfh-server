package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.AdminDTO;
import com.thfh.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 * 提供管理员的增删改查等管理功能
 */
@Api(tags = "管理员管理", description = "管理员相关的API接口，包括管理员查询、创建、更新和删除等功能")
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
    @ApiOperation(value = "获取管理员列表", notes = "根据查询条件获取管理员分页列表，支持多种筛选条件")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping
    public Result<Page<AdminDTO>> getAdmins(
            @ApiParam(value = "查询条件，包含分页信息和筛选条件") AdminDTO queryDTO) {
        return Result.success(adminService.getAdmins(queryDTO));
    }

    /**
     * 创建新管理员
     * @param adminDTO 管理员信息
     * @return 创建的管理员信息
     */
    @ApiOperation(value = "创建新管理员", notes = "创建一个新的管理员账号，需要提供管理员的基本信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "创建成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 409, message = "用户名已存在")
    })
    @PostMapping
    public Result<AdminDTO> createAdmin(
            @ApiParam(value = "管理员信息", required = true) @RequestBody AdminDTO adminDTO) {
        return Result.success(adminService.createAdmin(adminDTO));
    }

    /**
     * 更新管理员信息
     * @param id 管理员ID
     * @param adminDTO 更新的管理员信息
     * @return 更新后的管理员信息
     */
    @ApiOperation(value = "更新管理员信息", notes = "根据管理员ID更新管理员信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "管理员不存在"),
        @ApiResponse(code = 409, message = "新用户名已存在")
    })
    @PutMapping("/{id}")
    public Result<AdminDTO> updateAdmin(
            @ApiParam(value = "管理员ID", required = true) @PathVariable Long id,
            @ApiParam(value = "更新的管理员信息", required = true) @RequestBody AdminDTO adminDTO) {
        return Result.success(adminService.updateAdmin(id, adminDTO));
    }

    /**
     * 删除管理员
     * @param id 管理员ID
     * @return 操作结果
     */
    @ApiOperation(value = "删除管理员", notes = "根据管理员ID删除管理员")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "管理员不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> deleteAdmin(
            @ApiParam(value = "管理员ID", required = true) @PathVariable Long id) {
        adminService.deleteAdmin(id);
        return Result.success(null);
    }

    /**
     * 切换管理员状态（启用/禁用）
     * @param id 管理员ID
     * @return 操作结果
     */
    @ApiOperation(value = "切换管理员状态", notes = "启用或禁用管理员账号")
    @ApiResponses({
        @ApiResponse(code = 200, message = "操作成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "管理员不存在")
    })
    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleAdminStatus(
            @ApiParam(value = "管理员ID", required = true) @PathVariable Long id) {
        adminService.toggleAdminStatus(id);
        return Result.success(null);
    }
}
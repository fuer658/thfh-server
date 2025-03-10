package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.AdminDTO;
import com.thfh.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 * 提供管理员的增删改查等管理功能
 */
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
    @GetMapping
    public Result<Page<AdminDTO>> getAdmins(AdminDTO queryDTO) {
        return Result.success(adminService.getAdmins(queryDTO));
    }

    /**
     * 创建新管理员
     * @param adminDTO 管理员信息
     * @return 创建的管理员信息
     */
    @PostMapping
    public Result<AdminDTO> createAdmin(@RequestBody AdminDTO adminDTO) {
        return Result.success(adminService.createAdmin(adminDTO));
    }

    /**
     * 更新管理员信息
     * @param id 管理员ID
     * @param adminDTO 更新的管理员信息
     * @return 更新后的管理员信息
     */
    @PutMapping("/{id}")
    public Result<AdminDTO> updateAdmin(@PathVariable Long id, @RequestBody AdminDTO adminDTO) {
        return Result.success(adminService.updateAdmin(id, adminDTO));
    }

    /**
     * 删除管理员
     * @param id 管理员ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return Result.success(null);
    }

    /**
     * 切换管理员状态（启用/禁用）
     * @param id 管理员ID
     * @return 操作结果
     */
    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleAdminStatus(@PathVariable Long id) {
        adminService.toggleAdminStatus(id);
        return Result.success(null);
    }
}
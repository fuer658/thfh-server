package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.AdminDTO;
import com.thfh.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping
    public Result<Page<AdminDTO>> getAdmins(AdminDTO queryDTO) {
        return Result.success(adminService.getAdmins(queryDTO));
    }

    @PostMapping
    public Result<AdminDTO> createAdmin(@RequestBody AdminDTO adminDTO) {
        return Result.success(adminService.createAdmin(adminDTO));
    }

    @PutMapping("/{id}")
    public Result<AdminDTO> updateAdmin(@PathVariable Long id, @RequestBody AdminDTO adminDTO) {
        return Result.success(adminService.updateAdmin(id, adminDTO));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return Result.success(null);
    }

    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleAdminStatus(@PathVariable Long id) {
        adminService.toggleAdminStatus(id);
        return Result.success(null);
    }
}
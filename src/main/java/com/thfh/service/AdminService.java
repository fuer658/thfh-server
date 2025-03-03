package com.thfh.service;

import com.thfh.dto.AdminDTO;
import org.springframework.data.domain.Page;

public interface AdminService {
    /**
     * 获取管理员列表
     */
    Page<AdminDTO> getAdmins(AdminDTO queryDTO);

    /**
     * 创建管理员
     */
    AdminDTO createAdmin(AdminDTO adminDTO);

    /**
     * 更新管理员信息
     */
    AdminDTO updateAdmin(Long id, AdminDTO adminDTO);

    /**
     * 删除管理员
     */
    void deleteAdmin(Long id);

    /**
     * 切换管理员状态
     */
    void toggleAdminStatus(Long id);
}
package com.thfh.service;

import com.thfh.dto.AdminDTO;
import org.springframework.data.domain.Page;

/**
 * 管理员服务接口
 * 定义管理员相关的业务逻辑操作，包括管理员的创建、查询、修改、删除等功能
 * 以及管理员状态管理等功能
 */
public interface AdminService {
    /**
     * 获取管理员列表
     * @param queryDTO 查询条件对象，包含用户名、真实姓名、启用状态等过滤条件
     * @return 分页后的管理员DTO列表
     */
    Page<AdminDTO> getAdmins(AdminDTO queryDTO);

    /**
     * 创建管理员
     * @param adminDTO 管理员信息对象，包含管理员的基本信息
     * @return 创建成功的管理员DTO对象
     * @throws RuntimeException 当用户名已存在时抛出
     */
    AdminDTO createAdmin(AdminDTO adminDTO);

    /**
     * 更新管理员信息
     * @param id 管理员ID
     * @param adminDTO 更新后的管理员信息对象
     * @return 更新后的管理员DTO对象
     * @throws RuntimeException 当管理员不存在时抛出
     */
    AdminDTO updateAdmin(Long id, AdminDTO adminDTO);

    /**
     * 删除管理员
     * @param id 要删除的管理员ID
     */
    void deleteAdmin(Long id);

    /**
     * 切换管理员启用状态
     * 如果管理员当前是启用状态，则禁用；如果是禁用状态，则启用
     * @param id 管理员ID
     * @throws RuntimeException 当管理员不存在时抛出
     */
    void toggleAdminStatus(Long id);
}
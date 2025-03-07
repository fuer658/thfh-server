package com.thfh.repository;

import com.thfh.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * 管理员数据访问接口
 * 提供对管理员(Admin)实体的数据库操作功能
 * 继承自JpaRepository，自动提供基本的CRUD操作
 * 实现JpaSpecificationExecutor接口，支持复杂条件查询
 */
public interface AdminRepository extends JpaRepository<Admin, Long>, JpaSpecificationExecutor<Admin> {
    
    /**
     * 根据用户名查找管理员
     * 
     * @param username 管理员用户名
     * @return 包含管理员的Optional对象，如果不存在则为空
     */
    Optional<Admin> findByUsername(String username);
}
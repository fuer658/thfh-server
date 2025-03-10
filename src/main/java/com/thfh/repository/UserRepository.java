package com.thfh.repository;

import com.thfh.model.User;
import com.thfh.model.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * 用户数据访问接口
 * 提供对用户(User)实体的数据库操作功能
 * 继承自JpaRepository，自动提供基本的CRUD操作
 * 实现JpaSpecificationExecutor接口，支持复杂条件查询
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    /**
     * 检查指定用户名是否已存在
     * 
     * @param username 用户名
     * @return 如果用户名已存在返回true，否则返回false
     */
    boolean existsByUsername(String username);
    
    /**
     * 根据用户类型查询用户，支持分页
     * 
     * @param userType 用户类型
     * @param pageable 分页参数
     * @return 分页后的用户列表
     */
    Page<User> findByUserType(UserType userType, Pageable pageable);
    
    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 包含用户的Optional对象，如果不存在则为空
     */
    Optional<User> findByUsername(String username);
}
package com.thfh.repository;

import com.thfh.model.User;
import com.thfh.model.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 * 提供对用户(User)实体的数据库操作功能
 * 继承自JpaRepository，自动提供基本的CRUD操作
 * 实现JpaSpecificationExecutor接口，支持复杂条件查询
 */
@Repository
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

    long countByUserType(UserType userType);

    long countByUserTypeAndEnabled(UserType userType, Boolean enabled);
    
    /**
     * 根据公司ID查找企业用户
     * 
     * @param companyId 公司ID
     * @return 该公司的所有企业用户列表
     */
    @Query("SELECT u FROM User u WHERE u.company.id = :companyId AND u.userType = 'ENTERPRISE'")
    List<User> findByCompanyId(@Param("companyId") Long companyId);
    
    /**
     * 根据公司ID查找企业用户(分页)
     * 
     * @param companyId 公司ID
     * @param pageable 分页参数
     * @return 分页后的企业用户列表
     */
    @Query("SELECT u FROM User u WHERE u.company.id = :companyId AND u.userType = 'ENTERPRISE'")
    Page<User> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 根据手机号精确查找用户
     * @param phone 手机号
     * @return 用户对象
     */
    Optional<User> findByPhone(String phone);

    /**
     * 根据用户名模糊搜索用户
     * @param username 用户名
     * @return 匹配的用户列表
     */
    List<User> findByUsernameContaining(String username);
}
package com.thfh.repository;

import com.thfh.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * 公司数据访问接口
 * 提供对公司(Company)实体的数据库操作功能
 * 继承自JpaRepository，自动提供基本的CRUD操作
 * 实现JpaSpecificationExecutor接口，支持复杂条件查询
 */
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
    
    /**
     * 根据条件分页查询公司信息
     *
     * @param name 公司名称（模糊匹配，可为null）
     * @param enabled 启用状态（可为null）
     * @param pageable 分页参数
     * @return 分页后的公司列表
     */
    @Query("SELECT c FROM Company c WHERE " +
           "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:enabled IS NULL OR c.enabled = :enabled)")
    Page<Company> findByCondition(@Param("name") String name, @Param("enabled") Boolean enabled, Pageable pageable);

    /**
     * 更新公司启用状态
     * 
     * @param id 公司ID
     * @param enabled 启用状态
     */
    @Modifying
    @Query("UPDATE Company c SET c.enabled = :enabled WHERE c.id = :id")
    void updateStatus(@Param("id") Long id, @Param("enabled") Boolean enabled);

    /**
     * 查询所有启用状态的公司
     * 
     * @return 所有启用状态的公司列表
     */
    @Query("SELECT c FROM Company c WHERE c.enabled = true")
    List<Company> findAllEnabled();
}
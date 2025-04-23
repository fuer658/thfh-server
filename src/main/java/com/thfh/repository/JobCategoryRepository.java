package com.thfh.repository;

import com.thfh.model.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 职位分类数据访问接口
 */
@Repository
public interface JobCategoryRepository extends JpaRepository<JobCategory, Long> {
    
    /**
     * 查询所有顶级分类（parentId为null的分类）
     * @return 顶级分类列表
     */
    List<JobCategory> findByParentIdIsNullOrderBySortAsc();
    
    /**
     * 根据父分类ID查询子分类
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<JobCategory> findByParentIdOrderBySortAsc(Long parentId);
    
    /**
     * 查询所有启用的分类
     * @return 启用的分类列表
     */
    List<JobCategory> findByEnabledTrueOrderBySortAsc();
    
    /**
     * 查询所有启用的顶级分类
     * @return 启用的顶级分类列表
     */
    List<JobCategory> findByParentIdIsNullAndEnabledTrueOrderBySortAsc();
    
    /**
     * 根据父分类ID查询启用的子分类
     * @param parentId 父分类ID
     * @return 启用的子分类列表
     */
    List<JobCategory> findByParentIdAndEnabledTrueOrderBySortAsc(Long parentId);
    
    /**
     * 检查是否存在子分类
     * @param parentId 父分类ID
     * @return 存在返回true，不存在返回false
     */
    boolean existsByParentId(Long parentId);
} 
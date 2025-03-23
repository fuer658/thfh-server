package com.thfh.repository;

import com.thfh.model.CompanyAlbumCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyAlbumCategoryRepository extends JpaRepository<CompanyAlbumCategory, Long>, JpaSpecificationExecutor<CompanyAlbumCategory> {
    
    /**
     * 根据公司ID查询相册分类列表
     * @param companyId 公司ID
     * @return 相册分类列表
     */
    List<CompanyAlbumCategory> findByCompanyIdAndEnabledOrderByCreateTimeDesc(Long companyId, Boolean enabled);

    /**
     * 根据公司ID分页查询相册分类
     * @param companyId 公司ID
     * @param pageable 分页参数
     * @return 分页的相册分类列表
     */
    Page<CompanyAlbumCategory> findByCompanyId(Long companyId, Pageable pageable);

    /**
     * 根据公司ID和分类名称模糊查询
     * @param companyId 公司ID
     * @param name 分类名称
     * @param pageable 分页参数
     * @return 分页的相册分类列表
     */
    @Query("SELECT c FROM CompanyAlbumCategory c WHERE c.company.id = :companyId AND (:name IS NULL OR c.name LIKE %:name%)")
    Page<CompanyAlbumCategory> findByCompanyIdAndNameLike(@Param("companyId") Long companyId, @Param("name") String name, Pageable pageable);
} 
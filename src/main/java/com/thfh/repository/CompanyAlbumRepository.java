package com.thfh.repository;

import com.thfh.model.CompanyAlbum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyAlbumRepository extends JpaRepository<CompanyAlbum, Long>, JpaSpecificationExecutor<CompanyAlbum> {
    
    /**
     * 根据公司ID查询相册列表
     * @param companyId 公司ID
     * @return 相册列表
     */
    List<CompanyAlbum> findByCompanyIdAndEnabledOrderBySortOrderAscCreateTimeDesc(Long companyId, Boolean enabled);

    /**
     * 根据分类ID查询相册列表
     * @param categoryId 分类ID
     * @return 相册列表
     */
    List<CompanyAlbum> findByCategoryIdAndEnabledOrderBySortOrderAscCreateTimeDesc(Long categoryId, Boolean enabled);

    /**
     * 根据公司ID分页查询相册
     * @param companyId 公司ID
     * @param pageable 分页参数
     * @return 分页的相册列表
     */
    Page<CompanyAlbum> findByCompanyId(Long companyId, Pageable pageable);

    /**
     * 根据分类ID分页查询相册
     * @param categoryId 分类ID
     * @param pageable 分页参数
     * @return 分页的相册列表
     */
    Page<CompanyAlbum> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * 根据公司ID和标题模糊查询
     * @param companyId 公司ID
     * @param title 标题
     * @param pageable 分页参数
     * @return 分页的相册列表
     */
    @Query("SELECT a FROM CompanyAlbum a WHERE a.company.id = :companyId AND (:title IS NULL OR a.title LIKE %:title%)")
    Page<CompanyAlbum> findByCompanyIdAndTitleLike(@Param("companyId") Long companyId, @Param("title") String title, Pageable pageable);
} 
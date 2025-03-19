package com.thfh.repository;

import com.thfh.model.Artwork;
import com.thfh.model.ArtworkType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 艺术作品数据访问接口
 * 提供对艺术作品(Artwork)实体的数据库操作功能
 * 继承自JpaRepository，自动提供基本的CRUD操作
 */
@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    // 基本的CRUD操作由JpaRepository提供

    /**
     * 根据创建者ID查询作品，支持分页
     * 
     * @param creatorId 创建者ID
     * @param pageable 分页参数
     * @return 分页后的作品列表
     */
    Page<Artwork> findByCreatorId(Long creatorId, Pageable pageable);

    /**
     * 根据创建者ID和作品类型查询作品，支持分页
     * 
     * @param creatorId 创建者ID
     * @param type 作品类型
     * @param pageable 分页参数
     * @return 分页后的作品列表
     */
    Page<Artwork> findByCreatorIdAndType(Long creatorId, ArtworkType type, Pageable pageable);

    /**
     * 根据创建者ID和启用状态查询作品，支持分页
     * 
     * @param creatorId 创建者ID
     * @param enabled 启用状态
     * @param pageable 分页参数
     * @return 分页后的作品列表
     */
    Page<Artwork> findByCreatorIdAndEnabled(Long creatorId, Boolean enabled, Pageable pageable);

    /**
     * 根据创建者ID、作品类型和启用状态查询作品，支持分页
     * 
     * @param creatorId 创建者ID
     * @param type 作品类型
     * @param enabled 启用状态
     * @param pageable 分页参数
     * @return 分页后的作品列表
     */
    Page<Artwork> findByCreatorIdAndTypeAndEnabled(Long creatorId, ArtworkType type, Boolean enabled, Pageable pageable);

    /**
     * 根据标题模糊查询作品，支持分页
     * 
     * @param title 作品标题
     * @param pageable 分页参数
     * @return 分页后的作品列表
     */
    Page<Artwork> findByTitleContaining(String title, Pageable pageable);

    /**
     * 根据启用状态查询作品，支持分页
     * 
     * @param enabled 启用状态
     * @param pageable 分页参数
     * @return 分页后的作品列表
     */
    Page<Artwork> findByEnabled(Boolean enabled, Pageable pageable);

    /**
     * 根据标题和创建者ID查询作品，支持分页
     * 
     * @param title 作品标题
     * @param creatorId 创建者ID
     * @param pageable 分页参数
     * @return 分页后的作品列表
     */
    Page<Artwork> findByTitleContainingAndCreatorId(String title, Long creatorId, Pageable pageable);

    /**
     * 根据标题和启用状态查询作品，支持分页
     * 
     * @param title 作品标题
     * @param enabled 启用状态
     * @param pageable 分页参数
     * @return 分页后的作品列表
     */
    Page<Artwork> findByTitleContainingAndEnabled(String title, Boolean enabled, Pageable pageable);

    /**
     * 根据标题、创建者ID和启用状态查询作品，支持分页
     * 
     * @param title 作品标题
     * @param creatorId 创建者ID
     * @param enabled 启用状态
     * @param pageable 分页参数
     * @return 分页后的作品列表
     */
    Page<Artwork> findByTitleContainingAndCreatorIdAndEnabled(String title, Long creatorId, Boolean enabled, Pageable pageable);
}
package com.thfh.repository;

import com.thfh.model.Artwork;
import com.thfh.model.ArtworkType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    /**
     * 根据多个创作者ID查询已启用的作品，支持分页
     * 
     * @param creatorIds 创作者ID列表
     * @param pageable 分页参数
     * @return 分页后的作品列表
     */
    Page<Artwork> findByCreatorIdInAndEnabledTrue(List<Long> creatorIds, Pageable pageable);
    
    /**
     * 根据作品类型查询作品，支持分页
     *
     * @param type 作品类型
     * @param pageable 分页参数
     * @return 分页后的作品列表
     */
    Page<Artwork> findByType(ArtworkType type, Pageable pageable);
    
    /**
     * 搜索作品
     * 根据关键字（标题、描述、创作材料）和作品类型进行搜索
     *
     * @param keyword 搜索关键字
     * @param type 作品类型（可选）
     * @param enabled 是否启用
     * @param pageable 分页参数
     * @return 符合条件的作品列表
     */
    @Query("SELECT a FROM Artwork a WHERE (a.title LIKE %:keyword% OR a.description LIKE %:keyword% OR a.materials LIKE %:keyword%) " +
           "AND (:type IS NULL OR a.type = :type) " + 
           "AND (:enabled IS NULL OR a.enabled = :enabled)")
    Page<Artwork> searchArtworks(@Param("keyword") String keyword, @Param("type") ArtworkType type, @Param("enabled") Boolean enabled, Pageable pageable);
    
    /**
     * 根据标签ID搜索作品
     *
     * @param tagId 标签ID
     * @param enabled 是否启用
     * @param pageable 分页参数
     * @return 符合条件的作品列表
     */
    @Query("SELECT a FROM Artwork a JOIN a.tags t WHERE t.id = :tagId " + 
           "AND (:enabled IS NULL OR a.enabled = :enabled)")
    Page<Artwork> findByTagId(@Param("tagId") Long tagId, @Param("enabled") Boolean enabled, Pageable pageable);
    
    /**
     * 综合搜索作品
     * 根据关键字（标题、描述、创作材料）、标签ID和作品类型进行搜索
     *
     * @param keyword 搜索关键字
     * @param tagId 标签ID（可选）
     * @param type 作品类型（可选）
     * @param enabled 是否启用
     * @param pageable 分页参数
     * @return 符合条件的作品列表
     */
    @Query("SELECT DISTINCT a FROM Artwork a LEFT JOIN a.tags t WHERE " + 
           "((:keyword IS NULL OR a.title LIKE %:keyword% OR a.description LIKE %:keyword% OR a.materials LIKE %:keyword%) " +
           "AND (:tagId IS NULL OR t.id = :tagId) " +
           "AND (:type IS NULL OR a.type = :type) " +
           "AND (:enabled IS NULL OR a.enabled = :enabled))")
    Page<Artwork> searchArtworksComprehensive(@Param("keyword") String keyword, @Param("tagId") Long tagId, 
                                             @Param("type") ArtworkType type, @Param("enabled") Boolean enabled, Pageable pageable);
}
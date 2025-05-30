package com.thfh.repository;

import com.thfh.model.Artwork;
import com.thfh.model.ArtworkType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
    
    /**
     * 高级动态搜索作品
     * 支持多条件组合搜索，包括评分和价格区间
     *
     * @param keyword 关键字（匹配标题、描述、创作材料）
     * @param tagIds 标签ID列表
     * @param type 作品类型
     * @param creatorIds 创建者ID列表
     * @param minScore 最低评分
     * @param maxScore 最高评分
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param recommended 是否推荐
     * @param enabled 是否启用
     * @param pageable 分页参数
     * @return 符合条件的作品列表
     */
    @Query("SELECT DISTINCT a FROM Artwork a LEFT JOIN a.tags t WHERE " +
           "(:keyword IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.materials) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:#{#tagIds == null} = true OR t.id IN :tagIds) " +
           "AND (:type IS NULL OR a.type = :type) " +
           "AND (:#{#creatorIds == null} = true OR a.creator.id IN :creatorIds) " +
           "AND (:minScore IS NULL OR a.averageScore >= :minScore) " +
           "AND (:maxScore IS NULL OR a.averageScore <= :maxScore) " +
           "AND (:minPrice IS NULL OR a.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR a.price <= :maxPrice) " +
           "AND (:recommended IS NULL OR a.recommended = :recommended) " +
           "AND (:enabled IS NULL OR a.enabled = :enabled)")
    Page<Artwork> advancedSearch(
            @Param("keyword") String keyword,
            @Param("tagIds") List<Long> tagIds,
            @Param("type") ArtworkType type,
            @Param("creatorIds") List<Long> creatorIds,
            @Param("minScore") BigDecimal minScore,
            @Param("maxScore") BigDecimal maxScore,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("recommended") Boolean recommended,
            @Param("enabled") Boolean enabled,
            Pageable pageable);
            
    /**
     * 根据多个ID查询已启用的作品
     * 
     * @param ids 作品ID列表
     * @return 作品列表
     */
    List<Artwork> findByIdInAndEnabledTrue(List<Long> ids);
    
    /**
     * 查询评分高且浏览量大的热门作品，支持分页
     * 
     * @param pageable 分页参数
     * @return 热门作品列表
     */
    @Query("SELECT a FROM Artwork a WHERE a.enabled = true ORDER BY a.averageScore DESC, a.viewCount DESC")
    Page<Artwork> findByEnabledTrueOrderByAverageScoreDescViewCountDesc(Pageable pageable);

    /**
     * 查询最新发布的已启用作品，支持分页
     * 
     * @param pageable 分页参数
     * @return 最新作品列表
     */
    Page<Artwork> findByEnabledTrueOrderByCreateTimeDesc(Pageable pageable);

    /**
     * 查询编辑推荐的已启用作品（按更新时间倒序），支持分页
     * 
     * @param pageable 分页参数
     * @return 编辑推荐作品列表
     */
    Page<Artwork> findByEnabledTrueAndRecommendedTrueOrderByUpdateTimeDesc(Pageable pageable);
}
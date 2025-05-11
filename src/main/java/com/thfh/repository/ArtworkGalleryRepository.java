package com.thfh.repository;

import com.thfh.model.ArtworkGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 作品图册数据访问接口
 */
public interface ArtworkGalleryRepository extends JpaRepository<ArtworkGallery, Long> {
    
    /**
     * 根据作品ID查询图册列表
     * @param artworkId 作品ID
     * @return 图册列表
     */
    List<ArtworkGallery> findByArtworkIdOrderBySortOrderAsc(Long artworkId);
    
    /**
     * 根据作品ID删除图册
     * @param artworkId 作品ID
     */
    void deleteByArtworkId(Long artworkId);
    
    /**
     * 获取作品的最大排序序号
     * @param artworkId 作品ID
     * @return 最大排序序号
     */
    @Query("SELECT MAX(g.sortOrder) FROM ArtworkGallery g WHERE g.artwork.id = :artworkId")
    Integer findMaxSortOrderByArtworkId(@Param("artworkId") Long artworkId);
} 
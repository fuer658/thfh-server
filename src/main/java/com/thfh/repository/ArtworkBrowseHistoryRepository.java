package com.thfh.repository;

import com.thfh.model.ArtworkBrowseHistory;
import com.thfh.model.ArtworkType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtworkBrowseHistoryRepository extends JpaRepository<ArtworkBrowseHistory, Long> {
    
    /**
     * 根据用户ID查找浏览记录
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 用户的作品浏览记录
     */
    Page<ArtworkBrowseHistory> findByUserIdOrderByLastBrowseTimeDesc(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和作品ID查找浏览记录
     * @param userId 用户ID
     * @param artworkId 作品ID
     * @return 浏览记录
     */
    Optional<ArtworkBrowseHistory> findByUserIdAndArtworkId(Long userId, Long artworkId);
    
    /**
     * 查询用户最近浏览的作品ID列表
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 作品ID列表
     */
    @Query("SELECT h.artworkId FROM ArtworkBrowseHistory h WHERE h.userId = :userId ORDER BY h.lastBrowseTime DESC")
    List<Long> findRecentBrowsedArtworkIdsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 删除用户特定作品的浏览记录
     * @param userId 用户ID
     * @param artworkId 作品ID
     * @return 影响的行数
     */
    int deleteByUserIdAndArtworkId(Long userId, Long artworkId);
    
    /**
     * 删除用户的所有浏览记录
     * @param userId 用户ID
     * @return 影响的行数
     */
    int deleteByUserId(Long userId);
    
    /**
     * 根据用户ID和作品类型查询浏览记录
     * @param userId 用户ID
     * @param type 作品类型
     * @param pageable 分页参数
     * @return 用户的作品浏览记录
     */
    @Query("SELECT h FROM ArtworkBrowseHistory h JOIN Artwork a ON h.artworkId = a.id WHERE h.userId = :userId AND a.type = :type ORDER BY h.lastBrowseTime DESC")
    Page<ArtworkBrowseHistory> findByUserIdAndArtworkTypeOrderByLastBrowseTimeDesc(@Param("userId") Long userId, @Param("type") ArtworkType type, Pageable pageable);
    
    /**
     * 查询用户最近浏览的指定类型的作品ID列表
     * @param userId 用户ID
     * @param type 作品类型
     * @param pageable 分页参数
     * @return 作品ID列表
     */
    @Query("SELECT h.artworkId FROM ArtworkBrowseHistory h JOIN Artwork a ON h.artworkId = a.id WHERE h.userId = :userId AND a.type = :type ORDER BY h.lastBrowseTime DESC")
    List<Long> findRecentBrowsedArtworkIdsByUserIdAndType(@Param("userId") Long userId, @Param("type") ArtworkType type, Pageable pageable);
} 
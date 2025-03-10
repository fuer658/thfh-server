package com.thfh.repository;

import com.thfh.model.ArtworkScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 艺术作品评分数据访问接口
 * 提供对艺术作品评分(ArtworkScore)实体的数据库操作功能
 * 继承自JpaRepository，自动提供基本的CRUD操作
 */
public interface ArtworkScoreRepository extends JpaRepository<ArtworkScore, Long> {
    
    /**
     * 检查用户是否已对指定作品评分
     * 
     * @param artworkId 作品ID
     * @param userId 用户ID
     * @return 如果用户已对该作品评分返回true，否则返回false
     */
    @Query("SELECT COUNT(s) > 0 FROM ArtworkScore s WHERE s.artwork.id = :artworkId AND s.user.id = :userId")
    boolean existsByArtworkIdAndUserId(@Param("artworkId") Long artworkId, @Param("userId") Long userId);

    /**
     * 计算作品的平均评分
     * 
     * @param artworkId 作品ID
     * @return 包含平均评分的Optional对象，如果没有评分则为空
     */
    @Query("SELECT AVG(s.score) FROM ArtworkScore s WHERE s.artwork.id = :artworkId")
    Optional<BigDecimal> calculateAverageScore(@Param("artworkId") Long artworkId);

    /**
     * 统计作品的评分数量
     * 
     * @param artworkId 作品ID
     * @return 评分数量
     */
    @Query("SELECT COUNT(s) FROM ArtworkScore s WHERE s.artwork.id = :artworkId")
    long countByArtworkId(@Param("artworkId") Long artworkId);

    /**
     * 计算作品的总评分
     * 
     * @param artworkId 作品ID
     * @return 包含总评分的Optional对象，如果没有评分则为空
     */
    @Query("SELECT SUM(s.score) FROM ArtworkScore s WHERE s.artwork.id = :artworkId")
    Optional<BigDecimal> calculateTotalScore(@Param("artworkId") Long artworkId);
}
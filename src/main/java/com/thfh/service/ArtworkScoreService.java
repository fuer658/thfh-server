package com.thfh.service;

import com.thfh.model.Artwork;
import com.thfh.model.ArtworkScore;
import com.thfh.model.User;
import com.thfh.repository.ArtworkRepository;
import com.thfh.repository.ArtworkScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 作品评分服务类
 * 提供作品评分相关的业务逻辑处理，包括评分、更新评分统计信息、获取评分数据等功能
 */
@Service
public class ArtworkScoreService {

    @Autowired
    private ArtworkScoreRepository artworkScoreRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

    /**
     * 为作品评分
     * 创建新的评分记录，并更新作品的评分统计信息
     * @param artworkId 作品ID
     * @param userId 用户ID
     * @param score 评分值（0-100分）
     * @param user 用户对象
     * @param artwork 作品对象
     * @throws IllegalArgumentException 当评分不在有效范围内时抛出
     * @throws IllegalStateException 当用户已经对该作品评分过时抛出
     */
    @Transactional
    public void scoreArtwork(Long artworkId, Long userId, BigDecimal score, User user, Artwork artwork) {
        // 检查评分是否在有效范围内（0-100分）
        if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("评分必须在0到100分之间");
        }

        // 检查用户是否已经评分过
        if (artworkScoreRepository.existsByArtworkIdAndUserId(artworkId, userId)) {
            throw new IllegalStateException("您已经对这个作品评分过了");
        }

        // 创建新的评分记录
        ArtworkScore artworkScore = new ArtworkScore();
        artworkScore.setArtwork(artwork);
        artworkScore.setUser(user);
        artworkScore.setScore(score);

        // 保存评分记录
        artworkScoreRepository.save(artworkScore);

        // 更新作品的评分统计信息
        updateArtworkScoreStatistics(artwork);
    }

    /**
     * 更新作品的评分统计信息
     * 计算总评分人数、总分和平均分，并更新作品信息
     * @param artwork 需要更新评分统计的作品对象
     */
    @Transactional
    public void updateArtworkScoreStatistics(Artwork artwork) {
        // 获取总评分人数
        long scoreCount = artworkScoreRepository.countByArtworkId(artwork.getId());

        if (scoreCount > 0) {
            // 计算总分和平均分
            BigDecimal totalScore = artworkScoreRepository.calculateTotalScore(artwork.getId())
                    .orElse(BigDecimal.ZERO);
            BigDecimal averageScore = artworkScoreRepository.calculateAverageScore(artwork.getId())
                    .orElse(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);

            // 更新作品的评分信息
            artwork.setScoreCount((int) scoreCount);
            artwork.setTotalScore(totalScore);
            artwork.setAverageScore(averageScore);

            // 保存更新后的作品信息
            artworkRepository.save(artwork);
        }
    }

    /**
     * 获取作品的平均评分
     * @param artworkId 作品ID
     * @return 作品的平均评分，如果没有评分则返回0
     */
    public BigDecimal getArtworkAverageScore(Long artworkId) {
        return artworkScoreRepository.calculateAverageScore(artworkId)
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 获取作品的评分人数
     * @param artworkId 作品ID
     * @return 作品的评分人数
     */
    public long getArtworkScoreCount(Long artworkId) {
        return artworkScoreRepository.countByArtworkId(artworkId);
    }
}
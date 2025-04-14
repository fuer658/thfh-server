package com.thfh.repository;

import com.thfh.model.Artwork;
import com.thfh.model.ArtworkFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 艺术作品收藏数据访问接口
 * 提供对艺术作品收藏(ArtworkFavorite)实体的数据库操作功能
 * 继承自JpaRepository，自动提供基本的CRUD操作
 */
@Repository
public interface ArtworkFavoriteRepository extends JpaRepository<ArtworkFavorite, Long> {

    /**
     * 检查作品是否已被用户收藏
     * 
     * @param artworkId 作品ID
     * @param userId 用户ID
     * @return 如果用户已收藏该作品返回true，否则返回false
     */
    boolean existsByArtworkIdAndUserId(Long artworkId, Long userId);

    /**
     * 删除用户的作品收藏记录
     * 
     * @param artworkId 作品ID
     * @param userId 用户ID
     * @return 删除的记录数量
     */
    long deleteByArtworkIdAndUserId(Long artworkId, Long userId);

    /**
     * 获取用户收藏的作品列表
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页后的用户收藏作品列表
     */
    @Query("SELECT af.artwork FROM ArtworkFavorite af WHERE af.user.id = ?1 ORDER BY af.createTime DESC")
    Page<Artwork> findArtworksByUserId(Long userId, Pageable pageable);
}
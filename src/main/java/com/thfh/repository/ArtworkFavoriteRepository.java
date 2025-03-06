package com.thfh.repository;

import com.thfh.model.Artwork;
import com.thfh.model.ArtworkFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkFavoriteRepository extends JpaRepository<ArtworkFavorite, Long> {

    /**
     * 检查作品是否已被用户收藏
     */
    boolean existsByArtworkIdAndUserId(Long artworkId, Long userId);

    /**
     * 删除用户的作品收藏记录
     */
    long deleteByArtworkIdAndUserId(Long artworkId, Long userId);

    /**
     * 获取用户收藏的作品列表
     */
    @Query("SELECT af.artwork FROM ArtworkFavorite af WHERE af.user.id = ?1")
    Page<Artwork> findArtworksByUserId(Long userId, Pageable pageable);
}
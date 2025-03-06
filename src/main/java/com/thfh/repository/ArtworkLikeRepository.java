package com.thfh.repository;

import com.thfh.model.Artwork;
import com.thfh.model.ArtworkLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkLikeRepository extends JpaRepository<ArtworkLike, Long> {

    /**
     * 检查作品是否已被用户点赞
     */
    boolean existsByArtworkIdAndUserId(Long artworkId, Long userId);

    /**
     * 删除用户的作品点赞记录
     */
    long deleteByArtworkIdAndUserId(Long artworkId, Long userId);

    /**
     * 获取用户点赞的作品列表
     */
    @Query("SELECT al.artwork FROM ArtworkLike al WHERE al.user.id = ?1")
    Page<Artwork> findArtworksByUserId(Long userId, Pageable pageable);
}
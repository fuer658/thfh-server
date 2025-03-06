package com.thfh.service;

import com.thfh.model.Artwork;
import com.thfh.model.ArtworkFavorite;
import com.thfh.model.User;
import com.thfh.repository.ArtworkFavoriteRepository;
import com.thfh.repository.ArtworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArtworkFavoriteService {

    @Autowired
    private ArtworkFavoriteRepository artworkFavoriteRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

    /**
     * 添加收藏
     */
    @Transactional
    public void addFavorite(Long artworkId, User user) {
        // 检查是否已经收藏
        if (artworkFavoriteRepository.existsByArtworkIdAndUserId(artworkId, user.getId())) {
            throw new IllegalStateException("您已经收藏过该作品");
        }

        // 获取作品信息
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("作品不存在"));

        // 创建收藏记录
        ArtworkFavorite favorite = new ArtworkFavorite();
        favorite.setArtwork(artwork);
        favorite.setUser(user);
        artworkFavoriteRepository.save(favorite);

        // 更新作品收藏数
        if (artwork.getFavoriteCount() == null) {
            artwork.setFavoriteCount(0);
        }
        artwork.setFavoriteCount(artwork.getFavoriteCount() + 1);
        artworkRepository.save(artwork);
    }

    /**
     * 取消收藏
     */
    @Transactional
    public void removeFavorite(Long artworkId, User user) {
        // 获取作品信息
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("作品不存在"));

        // 删除收藏记录
        if (artworkFavoriteRepository.deleteByArtworkIdAndUserId(artworkId, user.getId()) > 0) {
            // 更新作品收藏数
            artwork.setFavoriteCount(artwork.getFavoriteCount() - 1);
            artworkRepository.save(artwork);
        }
    }

    /**
     * 获取用户收藏的作品列表
     */
    public Page<Artwork> getUserFavorites(Long userId, Pageable pageable) {
        return artworkFavoriteRepository.findArtworksByUserId(userId, pageable);
    }

    /**
     * 检查用户是否已收藏作品
     */
    public boolean isFavorited(Long artworkId, Long userId) {
        return artworkFavoriteRepository.existsByArtworkIdAndUserId(artworkId, userId);
    }
}
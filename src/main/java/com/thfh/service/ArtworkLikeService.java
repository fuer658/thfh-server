package com.thfh.service;

import com.thfh.model.Artwork;
import com.thfh.model.ArtworkLike;
import com.thfh.model.User;
import com.thfh.repository.ArtworkLikeRepository;
import com.thfh.repository.ArtworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArtworkLikeService {

    @Autowired
    private ArtworkLikeRepository artworkLikeRepository;

    @Autowired
    private ArtworkRepository artworkRepository;
    
    @Autowired
    private UserService userService;

    /**
     * 添加点赞
     */
    @Transactional
    public void addLike(Long artworkId, User user) {
        User currentUser = userService.getCurrentUser();
        // 检查是否已经点赞
        if (artworkLikeRepository.existsByArtworkIdAndUserId(artworkId, currentUser.getId())) {
            throw new IllegalStateException("您已经点赞过该作品");
        }

        // 获取作品信息
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("作品不存在"));

        // 创建点赞记录
        ArtworkLike like = new ArtworkLike();
        like.setArtwork(artwork);
        like.setUser(currentUser);
        artworkLikeRepository.save(like);

        // 更新作品点赞数
        if (artwork.getLikeCount() == null) {
            artwork.setLikeCount(0);
        }
        artwork.setLikeCount(artwork.getLikeCount() + 1);
        artworkRepository.save(artwork);
    }

    /**
     * 取消点赞
     */
    @Transactional
    public void removeLike(Long artworkId, User user) {
        User currentUser = userService.getCurrentUser();
        // 获取作品信息
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("作品不存在"));

        // 删除点赞记录
        if (artworkLikeRepository.deleteByArtworkIdAndUserId(artworkId, currentUser.getId()) > 0) {
            // 更新作品点赞数
            artwork.setLikeCount(artwork.getLikeCount() - 1);
            artworkRepository.save(artwork);
        }
    }

    /**
     * 获取用户点赞的作品列表
     */
    public Page<Artwork> getUserLikes(Long userId, Pageable pageable) {
        return artworkLikeRepository.findArtworksByUserId(userId, pageable);
    }

    /**
     * 检查用户是否已点赞作品
     */
    public boolean isLiked(Long artworkId, Long userId) {
        return artworkLikeRepository.existsByArtworkIdAndUserId(artworkId, userId);
    }
}
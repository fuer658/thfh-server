package com.thfh.service;

import com.thfh.model.Artwork;
import com.thfh.model.ArtworkTag;
import com.thfh.model.ArtworkType;
import com.thfh.model.User;
import com.thfh.repository.ArtworkRepository;
import com.thfh.repository.ArtworkTagRepository;
import com.thfh.repository.ArtworkScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class ArtworkService {

    @Autowired
    private ArtworkRepository artworkRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ArtworkTagRepository artworkTagRepository;

    /**
     * 创建作品
     * @param artwork 作品信息
     * @return 创建的作品
     */
    public Artwork createArtwork(Artwork artwork) {
        // 验证作品必填字段
        if (artwork.getTitle() == null || artwork.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("作品标题不能为空");
        }
        if (artwork.getType() == null) {
            throw new IllegalArgumentException("作品类型不能为空");
        }
        
        // 设置当前登录用户为作品创建者
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("用户未登录");
        }
        artwork.setCreator(currentUser);
        
        // 保存标签
        if (artwork.getTags() != null && !artwork.getTags().isEmpty()) {
            Set<ArtworkTag> processedTags = new HashSet<>();
            artwork.getTags().forEach(tag -> {
                if (tag.getId() == null) {
                    // 检查是否存在相同名称的标签
                    ArtworkTag existingTag = artworkTagRepository.findByName(tag.getName());
                    if (existingTag != null) {
                        // 如果存在，使用已有标签
                        processedTags.add(existingTag);
                    } else {
                        // 如果不存在，保存新标签
                        artworkTagRepository.save(tag);
                        processedTags.add(tag);
                    }
                } else {
                    processedTags.add(tag);
                }
            });
            artwork.setTags(processedTags);
        }
        
        // 保存作品
        return artworkRepository.save(artwork);
    }

    /**
     * 获取用户的作品列表
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 作品分页列表
     */
    public Page<Artwork> getUserArtworks(Long userId, Pageable pageable) {
        return artworkRepository.findByCreatorId(userId, pageable);
    }

    /**
     * 根据类型获取用户的作品列表
     * @param userId 用户ID
     * @param type 作品类型
     * @param pageable 分页参数
     * @return 作品分页列表
     */
    public Page<Artwork> getUserArtworksByType(Long userId, ArtworkType type, Pageable pageable) {
        return artworkRepository.findByCreatorIdAndType(userId, type, pageable);
    }

    /**
     * 根据启用状态获取用户的作品列表
     * @param userId 用户ID
     * @param enabled 是否启用
     * @param pageable 分页参数
     * @return 作品分页列表
     */
    public Page<Artwork> getUserArtworksByEnabled(Long userId, Boolean enabled, Pageable pageable) {
        return artworkRepository.findByCreatorIdAndEnabled(userId, enabled, pageable);
    }

    /**
     * 根据类型和启用状态获取用户的作品列表
     * @param userId 用户ID
     * @param type 作品类型
     * @param enabled 是否启用
     * @param pageable 分页参数
     * @return 作品分页列表
     */
    public Page<Artwork> getUserArtworksByTypeAndEnabled(Long userId, ArtworkType type, Boolean enabled, Pageable pageable) {
        return artworkRepository.findByCreatorIdAndTypeAndEnabled(userId, type, enabled, pageable);
    }

    /**
     * 根据ID获取作品信息
     * @param artworkId 作品ID
     * @return 作品信息
     */
    public java.util.Optional<Artwork> getArtworkById(Long artworkId) {
        return artworkRepository.findById(artworkId);
    }

    /**
     * 删除作品
     * @param artworkId 作品ID
     */
    @Transactional
    public void deleteArtwork(Long artworkId) {
        artworkRepository.deleteById(artworkId);
    }
}
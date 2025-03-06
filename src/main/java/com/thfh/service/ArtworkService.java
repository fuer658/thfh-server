package com.thfh.service;

import com.thfh.model.Artwork;
import com.thfh.model.ArtworkTag;
import com.thfh.model.ArtworkType;
import com.thfh.model.User;
import com.thfh.repository.ArtworkRepository;
import com.thfh.repository.ArtworkTagRepository;
import com.thfh.repository.ArtworkScoreRepository;
import com.thfh.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    /**
     * 创建作品
     * @param artwork 作品信息
     * @return 创建的作品
     */
    @Transactional
    public Artwork createArtwork(Artwork artwork) {
        try {
            System.out.println("开始创建作品，接收到的数据：" + artwork);
            
            // 验证作品必填字段
            if (artwork.getTitle() == null || artwork.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("作品标题不能为空");
            }
            if (artwork.getType() == null) {
                throw new IllegalArgumentException("作品类型不能为空");
            }
            if (artwork.getCreator() == null || artwork.getCreator().getId() == null) {
                throw new IllegalArgumentException("作品创建者不能为空");
            }

            // 验证并设置creator
            if (artwork.getCreator() == null || artwork.getCreator().getId() == null) {
                throw new IllegalArgumentException("作品创建者不能为空");
            }
            
            // 验证指定的creator是否存在
            User creator = userRepository.findById(artwork.getCreator().getId())
                    .orElseThrow(() -> new IllegalArgumentException("指定的创建者用户不存在"));
            artwork.setCreator(creator);
            System.out.println("设置后的创建者：" + artwork.getCreator());
            
            // 处理标签
            if (artwork.getTags() != null && !artwork.getTags().isEmpty()) {
                Set<ArtworkTag> processedTags = new HashSet<>();
                for (ArtworkTag tag : artwork.getTags()) {
                    if (tag.getId() == null) {
                        // 检查是否存在相同名称的标签
                        ArtworkTag existingTag = artworkTagRepository.findByName(tag.getName());
                        if (existingTag != null) {
                            processedTags.add(existingTag);
                        } else {
                            artworkTagRepository.save(tag);
                            processedTags.add(tag);
                        }
                    } else {
                        processedTags.add(tag);
                    }
                }
                artwork.setTags(processedTags);
            }
            
            // 保存作品
            return artworkRepository.save(artwork);
            
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("创建作品失败: " + e.getMessage(), e);
        }
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
     * 获取所有作品列表
     * @param pageable 分页参数
     * @return 作品分页列表
     */
    public Page<Artwork> getAllArtworks(Pageable pageable) {
        return artworkRepository.findAll(pageable);
    }

    /**
     * 删除作品
     * @param artworkId 作品ID
     */
    @Transactional
    public void deleteArtwork(Long artworkId) {
        artworkRepository.deleteById(artworkId);
    }

    /**
     * 添加新标签
     * @param tagName 标签名称
     * @return 创建的标签
     */
    @Transactional
    public ArtworkTag addTag(String tagName) {
        // 检查标签是否已存在
        ArtworkTag existingTag = artworkTagRepository.findByName(tagName);
        if (existingTag != null) {
            throw new IllegalStateException("标签已存在");
        }

        // 创建新标签
        ArtworkTag tag = new ArtworkTag();
        tag.setName(tagName);
        return artworkTagRepository.save(tag);
    }

    /**
     * 删除标签
     * @param tagId 标签ID
     */
    @Transactional
    public void removeTag(Long tagId) {
        ArtworkTag tag = artworkTagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("标签不存在"));

        // 删除标签与作品的关联关系
        artworkRepository.findAll().forEach(artwork -> {
            artwork.getTags().removeIf(t -> t.getId().equals(tagId));
            artworkRepository.save(artwork);
        });

        // 删除标签
        artworkTagRepository.delete(tag);
    }
}
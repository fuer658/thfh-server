package com.thfh.service;

import com.thfh.dto.ArtworkUpdateDTO;
import com.thfh.model.Artwork;
import com.thfh.model.ArtworkTag;
import com.thfh.model.ArtworkType;
import com.thfh.model.User;
import com.thfh.repository.ArtworkRepository;
import com.thfh.repository.ArtworkTagRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
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
        
        // 确保作品有创建者
        if (artwork.getCreator() == null) {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                throw new IllegalStateException("用户未登录");
            }
            artwork.setCreator(currentUser);
        }
        
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

    /**
     * 修改作品推荐状态
     * @param artworkId 作品ID
     * @param recommended 是否推荐
     * @return 更新后的作品
     */
    @Transactional
    public Artwork updateArtworkRecommendation(Long artworkId, Boolean recommended) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("作品不存在"));
        artwork.setRecommended(recommended);
        return artworkRepository.save(artwork);
    }

    /**
     * 修改商业作品价格
     * @param artworkId 作品ID
     * @param price 新价格
     * @return 更新后的作品
     */
    @Transactional
    public Artwork updateArtworkPrice(Long artworkId, BigDecimal price) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("作品不存在"));
        
        if (artwork.getType() != ArtworkType.COMMERCIAL) {
            throw new IllegalStateException("只有商业作品可以修改价格");
        }
        
        artwork.setPrice(price);
        return artworkRepository.save(artwork);
    }

    /**
     * 管理员更新作品信息
     * @param artworkId 作品ID
     * @param updateDTO 更新的作品信息
     * @return 更新后的作品
     */
    @Transactional
    public Artwork updateArtwork(Long artworkId, ArtworkUpdateDTO updateDTO) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("作品不存在"));
        
        // 更新基本信息
        BeanUtils.copyProperties(updateDTO, artwork, "id", "creator", "createTime", "averageScore", "scoreCount", "totalScore", "favoriteCount", "likeCount", "viewCount");
        
        // 处理标签
        if (updateDTO.getTags() != null) {
            Set<ArtworkTag> processedTags = new HashSet<>();
            updateDTO.getTags().forEach(tag -> {
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
        
        return artworkRepository.save(artwork);
    }

    /**
     * 增加作品浏览量
     * @param artworkId 作品ID
     * @return 更新后的作品
     */
    @Transactional
    public Artwork incrementViewCount(Long artworkId) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("作品不存在"));
        
        if (artwork.getViewCount() == null) {
            artwork.setViewCount(0);
        }
        artwork.setViewCount(artwork.getViewCount() + 1);
        return artworkRepository.save(artwork);
    }

    /**
     * 根据条件查询作品列表
     * @param title 作品标题（可选）
     * @param studentId 学生ID（可选）
     * @param enabled 是否启用（可选）
     * @param pageable 分页参数
     * @return 作品分页列表
     */
    public Page<Artwork> getArtworks(String title, Long studentId, Boolean enabled, Pageable pageable) {
        if (title != null && !title.isEmpty() && studentId != null && enabled != null) {
            return artworkRepository.findByTitleContainingAndCreatorIdAndEnabled(title, studentId, enabled, pageable);
        } else if (title != null && !title.isEmpty() && studentId != null) {
            return artworkRepository.findByTitleContainingAndCreatorId(title, studentId, pageable);
        } else if (title != null && !title.isEmpty() && enabled != null) {
            return artworkRepository.findByTitleContainingAndEnabled(title, enabled, pageable);
        } else if (studentId != null && enabled != null) {
            return artworkRepository.findByCreatorIdAndEnabled(studentId, enabled, pageable);
        } else if (title != null && !title.isEmpty()) {
            return artworkRepository.findByTitleContaining(title, pageable);
        } else if (studentId != null) {
            return artworkRepository.findByCreatorId(studentId, pageable);
        } else if (enabled != null) {
            return artworkRepository.findByEnabled(enabled, pageable);
        } else {
            return artworkRepository.findAll(pageable);
        }
    }

    /**
     * 获取指定创作者列表的作品
     * @param creatorIds 创作者ID列表
     * @param pageable 分页参数
     * @return 作品分页列表
     */
    public Page<Artwork> getArtworksByCreatorIds(List<Long> creatorIds, Pageable pageable) {
        return artworkRepository.findByCreatorIdInAndEnabledTrue(creatorIds, pageable);
    }

    /**
     * 根据用户ID获取作品列表
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 作品分页列表
     */
    public Page<Artwork> getArtworksByUserId(Long userId, Pageable pageable) {
        return artworkRepository.findByCreatorId(userId, pageable);
    }
}
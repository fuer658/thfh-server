package com.thfh.service;

import com.thfh.exception.BusinessException;
import com.thfh.exception.ErrorCode;
import com.thfh.model.Artwork;
import com.thfh.model.ArtworkBrowseHistory;
import com.thfh.model.User;
import com.thfh.model.ArtworkType;
import com.thfh.repository.ArtworkBrowseHistoryRepository;
import com.thfh.repository.ArtworkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

import com.thfh.dto.ArtworkBrowseHistoryDTO;
import com.thfh.dto.TagDTO;

@Slf4j
@Service
public class ArtworkBrowseHistoryService {

    private final ArtworkBrowseHistoryRepository artworkBrowseHistoryRepository;
    private final ArtworkRepository artworkRepository;
    private final UserService userService;
    private final ArtworkService artworkService;

    public ArtworkBrowseHistoryService(ArtworkBrowseHistoryRepository artworkBrowseHistoryRepository, ArtworkRepository artworkRepository, UserService userService, ArtworkService artworkService) {
        this.artworkBrowseHistoryRepository = artworkBrowseHistoryRepository;
        this.artworkRepository = artworkRepository;
        this.userService = userService;
        this.artworkService = artworkService;
    }

    /**
     * 记录或更新用户浏览作品的记录
     * 同时增加作品的浏览量
     *
     * @param artworkId 作品ID
     * @return 浏览记录
     */
    @Transactional
    public ArtworkBrowseHistory recordBrowseHistory(Long artworkId) {
        // 获取当前登录用户
        User currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        // 查询作品是否存在
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "作品不存在"));

        // 增加作品浏览量
        artworkService.incrementViewCount(artworkId);

        // 查询是否有浏览记录
        Optional<ArtworkBrowseHistory> existingRecord = artworkBrowseHistoryRepository.findByUserIdAndArtworkId(userId, artworkId);

        if (existingRecord.isPresent()) {
            // 更新现有记录
            ArtworkBrowseHistory history = existingRecord.get();
            history.setLastBrowseTime(LocalDateTime.now());
            history.setBrowseCount(history.getBrowseCount() + 1);
            return artworkBrowseHistoryRepository.save(history);
        } else {
            // 创建新记录
            ArtworkBrowseHistory newRecord = new ArtworkBrowseHistory();
            newRecord.setUserId(userId);
            newRecord.setArtworkId(artworkId);
            newRecord.setLastBrowseTime(LocalDateTime.now());
            newRecord.setBrowseCount(1);
            return artworkBrowseHistoryRepository.save(newRecord);
        }
    }

    /**
     * 获取用户的浏览历史
     *
     * @param pageable 分页参数
     * @return 浏览历史
     */
    public Page<Artwork> getUserBrowseHistory(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        
        // 获取用户浏览记录
        Page<ArtworkBrowseHistory> historyPage = artworkBrowseHistoryRepository.findByUserIdOrderByLastBrowseTimeDesc(
                currentUser.getId(), pageable);
        
        // 转换为作品列表
        return historyPage.map(history -> 
            artworkRepository.findById(history.getArtworkId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "作品不存在"))
        );
    }

    /**
     * 获取特定用户的浏览历史（管理员使用）
     *
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 浏览历史
     */
    public Page<Artwork> getUserBrowseHistoryByAdmin(Long userId, Pageable pageable) {
        // 验证用户是否存在
        userService.getUserById(userId);
        
        // 获取用户浏览记录
        Page<ArtworkBrowseHistory> historyPage = artworkBrowseHistoryRepository.findByUserIdOrderByLastBrowseTimeDesc(
                userId, pageable);
        
        // 转换为作品列表
        return historyPage.map(history -> 
            artworkRepository.findById(history.getArtworkId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "作品不存在"))
        );
    }

    /**
     * 获取用户最近浏览的作品
     *
     * @param limit 限制数量
     * @return 最近浏览作品列表
     */
    public List<Artwork> getRecentBrowsedArtworks(int limit) {
        User currentUser = userService.getCurrentUser();
        
        // 获取最近浏览的作品ID
        List<Long> artworkIds = artworkBrowseHistoryRepository.findRecentBrowsedArtworkIdsByUserId(
                currentUser.getId(), PageRequest.of(0, limit));
        
        // 获取对应的作品
        return artworkIds.stream()
                .map(artworkId -> artworkRepository.findById(artworkId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "作品不存在")))
                .collect(Collectors.toList());
    }

    /**
     * 删除单条浏览记录
     *
     * @param historyId 浏览记录ID
     */
    @Transactional
    public void deleteHistory(Long historyId) {
        User currentUser = userService.getCurrentUser();
        
        ArtworkBrowseHistory history = artworkBrowseHistoryRepository.findById(historyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "浏览记录不存在"));
        
        // 验证是否是用户自己的记录
        if (!history.getUserId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权删除该记录");
        }
        
        artworkBrowseHistoryRepository.delete(history);
    }

    /**
     * 管理员删除浏览记录
     *
     * @param historyId 浏览记录ID
     */
    @Transactional
    public void deleteHistoryByAdmin(Long historyId) {
        ArtworkBrowseHistory history = artworkBrowseHistoryRepository.findById(historyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "浏览记录不存在"));
        
        artworkBrowseHistoryRepository.delete(history);
    }

    /**
     * 清空用户的所有浏览记录
     */
    @Transactional
    public void clearUserHistory() {
        User currentUser = userService.getCurrentUser();
        artworkBrowseHistoryRepository.deleteByUserId(currentUser.getId());
    }

    /**
     * 管理员清空特定用户的浏览记录
     *
     * @param userId 用户ID
     */
    @Transactional
    public void clearUserHistoryByAdmin(Long userId) {
        // 验证用户是否存在
        userService.getUserById(userId);
        artworkBrowseHistoryRepository.deleteByUserId(userId);
    }

    /**
     * 获取用户特定类型作品的浏览历史
     *
     * @param type 作品类型
     * @param pageable 分页参数
     * @return 浏览历史
     */
    public Page<Artwork> getUserBrowseHistoryByType(ArtworkType type, Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        
        // 获取用户浏览记录
        Page<ArtworkBrowseHistory> historyPage = artworkBrowseHistoryRepository.findByUserIdAndArtworkTypeOrderByLastBrowseTimeDesc(
                currentUser.getId(), type, pageable);
        
        // 转换为作品列表
        return historyPage.map(history -> 
            artworkRepository.findById(history.getArtworkId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "作品不存在"))
        );
    }

    /**
     * 管理员获取用户特定类型作品的浏览历史
     *
     * @param userId 用户ID
     * @param type 作品类型
     * @param pageable 分页参数
     * @return 浏览历史
     */
    public Page<Artwork> getUserBrowseHistoryByTypeForAdmin(Long userId, ArtworkType type, Pageable pageable) {
        // 验证用户是否存在
        userService.getUserById(userId);
        
        // 获取用户浏览记录
        Page<ArtworkBrowseHistory> historyPage = artworkBrowseHistoryRepository.findByUserIdAndArtworkTypeOrderByLastBrowseTimeDesc(
                userId, type, pageable);
        
        // 转换为作品列表
        return historyPage.map(history -> 
            artworkRepository.findById(history.getArtworkId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "作品不存在"))
        );
    }

    /**
     * 获取用户最近浏览的特定类型的作品
     *
     * @param type 作品类型
     * @param limit 限制数量
     * @return 最近浏览作品列表
     */
    public List<Artwork> getRecentBrowsedArtworksByType(ArtworkType type, int limit) {
        User currentUser = userService.getCurrentUser();
        
        // 获取最近浏览的作品ID
        List<Long> artworkIds = artworkBrowseHistoryRepository.findRecentBrowsedArtworkIdsByUserIdAndType(
                currentUser.getId(), type, PageRequest.of(0, limit));
        
        // 获取对应的作品
        return artworkIds.stream()
                .map(artworkId -> artworkRepository.findById(artworkId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "作品不存在")))
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的浏览历史（使用精简DTO）
     *
     * @param pageable 分页参数
     * @return 浏览历史DTO
     */
    public Page<ArtworkBrowseHistoryDTO> getUserBrowseHistoryDTO(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        
        // 获取用户浏览记录
        Page<ArtworkBrowseHistory> historyPage = artworkBrowseHistoryRepository.findByUserIdOrderByLastBrowseTimeDesc(
                currentUser.getId(), pageable);
        
        // 转换为DTO
        return historyPage.map(this::convertToArtworkBrowseHistoryDTO);
    }
    
    /**
     * 获取用户特定类型作品的浏览历史（使用精简DTO）
     *
     * @param type 作品类型
     * @param pageable 分页参数
     * @return 浏览历史DTO
     */
    public Page<ArtworkBrowseHistoryDTO> getUserBrowseHistoryByTypeDTO(ArtworkType type, Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        
        // 获取用户浏览记录
        Page<ArtworkBrowseHistory> historyPage = artworkBrowseHistoryRepository.findByUserIdAndArtworkTypeOrderByLastBrowseTimeDesc(
                currentUser.getId(), type, pageable);
        
        // 转换为DTO
        return historyPage.map(this::convertToArtworkBrowseHistoryDTO);
    }
    
    /**
     * 获取用户最近浏览的作品（使用精简DTO）
     *
     * @param limit 限制数量
     * @return 最近浏览作品DTO列表
     */
    public List<ArtworkBrowseHistoryDTO> getRecentBrowsedArtworksDTO(int limit) {
        User currentUser = userService.getCurrentUser();
        
        // 获取最近浏览的作品ID
        List<Long> artworkIds = artworkBrowseHistoryRepository.findRecentBrowsedArtworkIdsByUserId(
                currentUser.getId(), PageRequest.of(0, limit));
        
        if (artworkIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取对应的浏览记录和作品信息
        List<ArtworkBrowseHistoryDTO> dtos = new ArrayList<>();
        for (Long artworkId : artworkIds) {
            ArtworkBrowseHistory history = artworkBrowseHistoryRepository.findByUserIdAndArtworkId(currentUser.getId(), artworkId)
                    .orElse(null);
            if (history != null) {
                dtos.add(convertToArtworkBrowseHistoryDTO(history));
            }
        }
        
        return dtos;
    }
    
    /**
     * 获取用户最近浏览的特定类型的作品（使用精简DTO）
     *
     * @param type 作品类型
     * @param limit 限制数量
     * @return 最近浏览作品DTO列表
     */
    public List<ArtworkBrowseHistoryDTO> getRecentBrowsedArtworksByTypeDTO(ArtworkType type, int limit) {
        User currentUser = userService.getCurrentUser();
        
        // 获取最近浏览的作品ID
        List<Long> artworkIds = artworkBrowseHistoryRepository.findRecentBrowsedArtworkIdsByUserIdAndType(
                currentUser.getId(), type, PageRequest.of(0, limit));
        
        if (artworkIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取对应的浏览记录和作品信息
        List<ArtworkBrowseHistoryDTO> dtos = new ArrayList<>();
        for (Long artworkId : artworkIds) {
            ArtworkBrowseHistory history = artworkBrowseHistoryRepository.findByUserIdAndArtworkId(currentUser.getId(), artworkId)
                    .orElse(null);
            if (history != null) {
                dtos.add(convertToArtworkBrowseHistoryDTO(history));
            }
        }
        
        return dtos;
    }
    
    /**
     * 管理员获取用户的浏览历史（使用精简DTO）
     *
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 浏览历史DTO
     */
    public Page<ArtworkBrowseHistoryDTO> getUserBrowseHistoryByAdminDTO(Long userId, Pageable pageable) {
        // 验证用户是否存在
        userService.getUserById(userId);
        
        // 获取用户浏览记录
        Page<ArtworkBrowseHistory> historyPage = artworkBrowseHistoryRepository.findByUserIdOrderByLastBrowseTimeDesc(
                userId, pageable);
        
        // 转换为DTO
        return historyPage.map(this::convertToArtworkBrowseHistoryDTO);
    }
    
    /**
     * 管理员获取用户特定类型作品的浏览历史（使用精简DTO）
     *
     * @param userId 用户ID
     * @param type 作品类型
     * @param pageable 分页参数
     * @return 浏览历史DTO
     */
    public Page<ArtworkBrowseHistoryDTO> getUserBrowseHistoryByTypeForAdminDTO(Long userId, ArtworkType type, Pageable pageable) {
        // 验证用户是否存在
        userService.getUserById(userId);
        
        // 获取用户浏览记录
        Page<ArtworkBrowseHistory> historyPage = artworkBrowseHistoryRepository.findByUserIdAndArtworkTypeOrderByLastBrowseTimeDesc(
                userId, type, pageable);
        
        // 转换为DTO
        return historyPage.map(this::convertToArtworkBrowseHistoryDTO);
    }
    
    /**
     * 将ArtworkBrowseHistory转换为ArtworkBrowseHistoryDTO
     * @param history 浏览记录
     * @return 浏览记录DTO
     */
    private ArtworkBrowseHistoryDTO convertToArtworkBrowseHistoryDTO(ArtworkBrowseHistory history) {
        ArtworkBrowseHistoryDTO dto = new ArtworkBrowseHistoryDTO();
        dto.setHistoryId(history.getId());
        dto.setArtworkId(history.getArtworkId());
        dto.setLastBrowseTime(history.getLastBrowseTime());
        dto.setBrowseCount(history.getBrowseCount());
        
        // 填充作品信息
        Artwork artwork = artworkRepository.findById(history.getArtworkId())
                .orElse(null);
        
        if (artwork != null) {
            dto.setTitle(artwork.getTitle());
            dto.setDescription(artwork.getDescription());
            dto.setCoverUrl(artwork.getCoverUrl());
            dto.setType(artwork.getType());
            dto.setAverageScore(artwork.getAverageScore());
            dto.setViewCount(artwork.getViewCount());
            
            // 创作者信息
            if (artwork.getCreator() != null) {
                dto.setCreatorId(artwork.getCreator().getId());
                dto.setCreatorName(artwork.getCreator().getUsername());
                dto.setCreatorAvatar(artwork.getCreator().getAvatar());
            }
            
            // 标签信息
            if (artwork.getTags() != null && !artwork.getTags().isEmpty()) {
                Set<TagDTO> tagDTOs = new HashSet<>();
                artwork.getTags().forEach(tag -> {
                    TagDTO tagDTO = new TagDTO();
                    tagDTO.setId(tag.getId());
                    tagDTO.setTagName(tag.getName());
                    tagDTOs.add(tagDTO);
                });
                dto.setTags(tagDTOs);
            }
        }
        
        return dto;
    }
} 
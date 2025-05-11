package com.thfh.service;

import com.thfh.dto.ArtworkGalleryDTO;
import com.thfh.model.Artwork;
import com.thfh.model.ArtworkGallery;
import com.thfh.repository.ArtworkGalleryRepository;
import com.thfh.repository.ArtworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 作品图册服务
 */
@Service
public class ArtworkGalleryService {

    @Autowired
    private ArtworkGalleryRepository artworkGalleryRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

    @Autowired
    private OrderCommentFileService fileService;

    /**
     * 添加图册图片
     * @param artworkId 作品ID
     * @param file 图片文件
     * @param description 图片描述
     * @return 图册信息
     */
    @Transactional
    public ArtworkGalleryDTO addGalleryImage(Long artworkId, MultipartFile file, String description) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new RuntimeException("作品不存在"));

        try {
            // 上传图片
            String imageUrl = fileService.uploadImage(file);

            // 获取当前最大排序序号
            Integer maxSortOrder = artworkGalleryRepository.findMaxSortOrderByArtworkId(artworkId);
            int nextSortOrder = (maxSortOrder == null ? 0 : maxSortOrder) + 1;

            // 创建图册记录
            ArtworkGallery gallery = new ArtworkGallery();
            gallery.setArtwork(artwork);
            gallery.setImageUrl(imageUrl);
            gallery.setDescription(description);
            gallery.setSortOrder(nextSortOrder);

            gallery = artworkGalleryRepository.save(gallery);
            return toDTO(gallery);
        } catch (IOException e) {
            throw new RuntimeException("上传图片失败: " + e.getMessage());
        }
    }

    /**
     * 获取作品的图册列表
     * @param artworkId 作品ID
     * @return 图册列表
     */
    @Transactional(readOnly = true)
    public List<ArtworkGalleryDTO> getGalleryByArtworkId(Long artworkId) {
        List<ArtworkGallery> galleries = artworkGalleryRepository.findByArtworkIdOrderBySortOrderAsc(artworkId);
        return galleries.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 删除图册图片
     * @param galleryId 图册ID
     */
    @Transactional
    public void deleteGalleryImage(Long galleryId) {
        ArtworkGallery gallery = artworkGalleryRepository.findById(galleryId)
                .orElseThrow(() -> new RuntimeException("图册不存在"));

        // 删除图片文件
        fileService.deleteFile(gallery.getImageUrl());

        // 删除数据库记录
        artworkGalleryRepository.delete(gallery);
    }

    /**
     * 更新图册图片排序
     * @param galleryId 图册ID
     * @param newSortOrder 新的排序序号
     */
    @Transactional
    public void updateGallerySortOrder(Long galleryId, Integer newSortOrder) {
        ArtworkGallery gallery = artworkGalleryRepository.findById(galleryId)
                .orElseThrow(() -> new RuntimeException("图册不存在"));

        gallery.setSortOrder(newSortOrder);
        artworkGalleryRepository.save(gallery);
    }

    /**
     * 实体转DTO
     */
    private ArtworkGalleryDTO toDTO(ArtworkGallery gallery) {
        if (gallery == null) return null;
        
        ArtworkGalleryDTO dto = new ArtworkGalleryDTO();
        dto.setId(gallery.getId());
        dto.setImageUrl(gallery.getImageUrl());
        dto.setDescription(gallery.getDescription());
        dto.setSortOrder(gallery.getSortOrder());
        dto.setCreateTime(gallery.getCreateTime());
        dto.setUpdateTime(gallery.getUpdateTime());
        return dto;
    }
} 
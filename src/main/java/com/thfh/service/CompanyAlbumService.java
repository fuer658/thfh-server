package com.thfh.service;

import com.thfh.dto.CompanyAlbumDTO;
import com.thfh.model.CompanyAlbum;
import com.thfh.model.Company;
import com.thfh.model.CompanyAlbumCategory;
import com.thfh.repository.CompanyAlbumRepository;
import com.thfh.repository.CompanyRepository;
import com.thfh.repository.CompanyAlbumCategoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyAlbumService {
    @Autowired
    private CompanyAlbumRepository albumRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private CompanyAlbumCategoryRepository categoryRepository;

    /**
     * 创建相册
     * @param albumDTO 相册信息
     * @return 创建的相册信息
     */
    @Transactional
    public CompanyAlbumDTO createAlbum(CompanyAlbumDTO albumDTO) {
        try {
            if (albumDTO == null) {
                throw new RuntimeException("相册信息不能为空");
            }
            if (albumDTO.getCompanyId() == null) {
                throw new RuntimeException("公司ID不能为空");
            }
            if (albumDTO.getTitle() == null || albumDTO.getTitle().trim().isEmpty()) {
                throw new RuntimeException("相册标题不能为空");
            }
            if (albumDTO.getImageUrl() == null || albumDTO.getImageUrl().trim().isEmpty()) {
                throw new RuntimeException("图片URL不能为空");
            }

            Company company = companyRepository.findById(albumDTO.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("公司不存在"));
                    
            CompanyAlbumCategory category = null;
            if (albumDTO.getCategoryId() != null) {
                category = categoryRepository.findById(albumDTO.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("分类不存在"));
            }

            CompanyAlbum album = new CompanyAlbum();
            BeanUtils.copyProperties(albumDTO, album, "id", "createTime", "updateTime");
            album.setCompany(company);
            album.setCategory(category);
            album.setCreateTime(LocalDateTime.now());
            album.setUpdateTime(LocalDateTime.now());
            album.setEnabled(true);
            album.setSortOrder(0);
            
            album = albumRepository.save(album);

            BeanUtils.copyProperties(album, albumDTO);
            if (category != null) {
                albumDTO.setCategoryName(category.getName());
            }
            return albumDTO;
        } catch (Exception e) {
            throw new RuntimeException("创建相册失败: " + e.getMessage());
        }
    }

    /**
     * 更新相册
     * @param id 相册ID
     * @param albumDTO 更新的相册信息
     * @return 更新后的相册信息
     */
    @Transactional
    public CompanyAlbumDTO updateAlbum(Long id, CompanyAlbumDTO albumDTO) {
        CompanyAlbum album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("相册不存在"));

        CompanyAlbumCategory category = null;
        if (albumDTO.getCategoryId() != null) {
            category = categoryRepository.findById(albumDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("分类不存在"));
        }

        BeanUtils.copyProperties(albumDTO, album, "id", "createTime", "company");
        if (category != null) {
            album.setCategory(category);
        }
        album = albumRepository.save(album);

        BeanUtils.copyProperties(album, albumDTO);
        if (album.getCategory() != null) {
            albumDTO.setCategoryName(album.getCategory().getName());
        }
        return albumDTO;
    }

    /**
     * 删除相册
     * @param id 相册ID
     */
    @Transactional
    public void deleteAlbum(Long id) {
        albumRepository.deleteById(id);
    }

    /**
     * 获取相册列表
     * @param companyId 公司ID
     * @return 相册列表
     */
    public List<CompanyAlbumDTO> getAlbums(Long companyId) {
        List<CompanyAlbum> albums = albumRepository.findByCompanyIdAndEnabledOrderBySortOrderAscCreateTimeDesc(companyId, true);
        return albums.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取分类下的相册列表
     * @param categoryId 分类ID
     * @return 相册列表
     */
    public List<CompanyAlbumDTO> getAlbumsByCategory(Long categoryId) {
        List<CompanyAlbum> albums = albumRepository.findByCategoryIdAndEnabledOrderBySortOrderAscCreateTimeDesc(categoryId, true);
        return albums.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询相册
     * @param companyId 公司ID
     * @param title 标题（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页的相册列表
     */
    public Page<CompanyAlbumDTO> getAlbumsPage(Long companyId, String title, int pageNum, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<CompanyAlbum> albumPage = albumRepository.findByCompanyIdAndTitleLike(companyId, title, pageRequest);
        return albumPage.map(this::convertToDTO);
    }

    /**
     * 分页查询分类下的相册
     * @param categoryId 分类ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页的相册列表
     */
    public Page<CompanyAlbumDTO> getAlbumsPageByCategory(Long categoryId, int pageNum, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<CompanyAlbum> albumPage = albumRepository.findByCategoryId(categoryId, pageRequest);
        return albumPage.map(this::convertToDTO);
    }

    /**
     * 将实体转换为DTO
     * @param album 相册实体
     * @return 相册DTO
     */
    private CompanyAlbumDTO convertToDTO(CompanyAlbum album) {
        CompanyAlbumDTO dto = new CompanyAlbumDTO();
        BeanUtils.copyProperties(album, dto);
        dto.setCompanyId(album.getCompany().getId());
        if (album.getCategory() != null) {
            dto.setCategoryId(album.getCategory().getId());
            dto.setCategoryName(album.getCategory().getName());
        }
        return dto;
    }
} 
package com.thfh.service;

import com.thfh.dto.CompanyAlbumCategoryDTO;
import com.thfh.model.CompanyAlbumCategory;
import com.thfh.model.Company;
import com.thfh.repository.CompanyAlbumCategoryRepository;
import com.thfh.repository.CompanyRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyAlbumCategoryService {
    @Autowired
    private CompanyAlbumCategoryRepository categoryRepository;
    
    @Autowired
    private CompanyRepository companyRepository;

    /**
     * 创建相册分类
     * @param categoryDTO 分类信息
     * @return 创建的分类信息
     */
    @Transactional
    public CompanyAlbumCategoryDTO createCategory(CompanyAlbumCategoryDTO categoryDTO) {
        Company company = companyRepository.findById(categoryDTO.getCompanyId())
                .orElseThrow(() -> new RuntimeException("公司不存在"));

        CompanyAlbumCategory category = new CompanyAlbumCategory();
        BeanUtils.copyProperties(categoryDTO, category, "id", "createTime", "updateTime", "enabled");
        category.setCompany(company);
        category = categoryRepository.save(category);

        BeanUtils.copyProperties(category, categoryDTO);
        return categoryDTO;
    }

    /**
     * 更新相册分类
     * @param id 分类ID
     * @param categoryDTO 更新的分类信息
     * @return 更新后的分类信息
     */
    @Transactional
    public CompanyAlbumCategoryDTO updateCategory(Long id, CompanyAlbumCategoryDTO categoryDTO) {
        CompanyAlbumCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));

        BeanUtils.copyProperties(categoryDTO, category, "id", "createTime", "company");
        category = categoryRepository.save(category);

        BeanUtils.copyProperties(category, categoryDTO);
        return categoryDTO;
    }

    /**
     * 删除相册分类
     * @param id 分类ID
     */
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    /**
     * 获取相册分类列表
     * @param companyId 公司ID
     * @return 分类列表
     */
    public List<CompanyAlbumCategoryDTO> getCategories(Long companyId) {
        List<CompanyAlbumCategory> categories = categoryRepository.findByCompanyIdAndEnabledOrderByCreateTimeDesc(companyId, true);
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询相册分类
     * @param companyId 公司ID
     * @param name 分类名称（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页的分类列表
     */
    public Page<CompanyAlbumCategoryDTO> getCategoriesPage(Long companyId, String name, int pageNum, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<CompanyAlbumCategory> categoryPage = categoryRepository.findByCompanyIdAndNameLike(companyId, name, pageRequest);
        return categoryPage.map(this::convertToDTO);
    }

    /**
     * 将实体转换为DTO
     * @param category 分类实体
     * @return 分类DTO
     */
    private CompanyAlbumCategoryDTO convertToDTO(CompanyAlbumCategory category) {
        CompanyAlbumCategoryDTO dto = new CompanyAlbumCategoryDTO();
        BeanUtils.copyProperties(category, dto);
        dto.setCompanyId(category.getCompany().getId());
        return dto;
    }
} 
package com.thfh.service;

import com.thfh.dto.JobCategoryDTO;
import com.thfh.model.JobCategory;
import com.thfh.repository.JobCategoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 职位分类服务类
 * 提供职位分类的增删改查等功能
 */
@Service
public class JobCategoryService {
    @Autowired
    private JobCategoryRepository jobCategoryRepository;

    /**
     * 获取所有职位分类（树形结构）
     * @return 职位分类树形列表
     */
    public List<JobCategoryDTO> getAllCategoriesTree() {
        // 获取所有顶级分类
        List<JobCategory> rootCategories = jobCategoryRepository.findByParentIdIsNullOrderBySortAsc();
        
        // 转换为DTO并构建树形结构
        return buildCategoryTree(rootCategories);
    }

    /**
     * 获取所有启用的职位分类（树形结构）
     * @return 启用的职位分类树形列表
     */
    public List<JobCategoryDTO> getEnabledCategoriesTree() {
        // 获取所有启用的顶级分类
        List<JobCategory> rootCategories = jobCategoryRepository.findByParentIdIsNullAndEnabledTrueOrderBySortAsc();
        
        // 转换为DTO并构建树形结构
        return buildCategoryTree(rootCategories);
    }

    /**
     * 根据ID获取职位分类
     * @param id 分类ID
     * @return 职位分类DTO
     */
    public JobCategoryDTO getCategoryById(Long id) {
        JobCategory category = jobCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位分类不存在"));
        
        return convertToDTO(category);
    }

    /**
     * 创建职位分类
     * @param categoryDTO 职位分类信息
     * @return 创建后的职位分类DTO
     */
    @Transactional
    public JobCategoryDTO createCategory(JobCategoryDTO categoryDTO) {
        // 如果有父分类ID，验证父分类是否存在
        if (categoryDTO.getParentId() != null) {
            jobCategoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new RuntimeException("父分类不存在"));
        }
        
        JobCategory category = new JobCategory();
        BeanUtils.copyProperties(categoryDTO, category, "id", "createTime", "updateTime", "children");
        
        category = jobCategoryRepository.save(category);
        
        return convertToDTO(category);
    }

    /**
     * 更新职位分类
     * @param id 分类ID
     * @param categoryDTO 更新的职位分类信息
     * @return 更新后的职位分类DTO
     */
    @Transactional
    public JobCategoryDTO updateCategory(Long id, JobCategoryDTO categoryDTO) {
        JobCategory category = jobCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位分类不存在"));
        
        // 如果有父分类ID，验证父分类是否存在
        if (categoryDTO.getParentId() != null) {
            // 不能将分类的父分类设置为自己
            if (categoryDTO.getParentId().equals(id)) {
                throw new RuntimeException("不能将分类的父分类设置为自己");
            }
            
            // 不能将分类的父分类设置为其子分类
            if (isChildCategory(id, categoryDTO.getParentId())) {
                throw new RuntimeException("不能将分类的父分类设置为其子分类");
            }
            
            jobCategoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new RuntimeException("父分类不存在"));
        }
        
        BeanUtils.copyProperties(categoryDTO, category, "id", "createTime", "updateTime", "children");
        
        category = jobCategoryRepository.save(category);
        
        return convertToDTO(category);
    }

    /**
     * 删除职位分类
     * @param id 分类ID
     */
    @Transactional
    public void deleteCategory(Long id) {
        // 验证分类是否存在
        JobCategory category = jobCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位分类不存在"));
        
        // 检查是否有子分类
        if (jobCategoryRepository.existsByParentId(id)) {
            throw new RuntimeException("该分类下有子分类，不能删除");
        }
        
        // 删除分类
        jobCategoryRepository.delete(category);
    }

    /**
     * 切换职位分类启用状态
     * @param id 分类ID
     * @return 更新后的职位分类DTO
     */
    @Transactional
    public JobCategoryDTO toggleCategoryStatus(Long id) {
        JobCategory category = jobCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位分类不存在"));
        
        category.setEnabled(!category.getEnabled());
        category = jobCategoryRepository.save(category);
        
        return convertToDTO(category);
    }

    /**
     * 构建分类树形结构
     * @param rootCategories 顶级分类列表
     * @return 树形结构的分类DTO列表
     */
    private List<JobCategoryDTO> buildCategoryTree(List<JobCategory> rootCategories) {
        List<JobCategoryDTO> result = new ArrayList<>();
        
        for (JobCategory rootCategory : rootCategories) {
            JobCategoryDTO rootDTO = convertToDTO(rootCategory);
            
            // 递归获取子分类
            List<JobCategory> children = jobCategoryRepository.findByParentIdOrderBySortAsc(rootCategory.getId());
            if (!children.isEmpty()) {
                rootDTO.setChildren(buildCategoryTree(children));
            }
            
            result.add(rootDTO);
        }
        
        return result;
    }

    /**
     * 判断targetId是否是sourceId的子分类
     * @param sourceId 源分类ID
     * @param targetId 目标分类ID
     * @return 如果targetId是sourceId的子分类，返回true；否则返回false
     */
    private boolean isChildCategory(Long sourceId, Long targetId) {
        List<JobCategory> children = jobCategoryRepository.findByParentIdOrderBySortAsc(sourceId);
        
        for (JobCategory child : children) {
            if (child.getId().equals(targetId) || isChildCategory(child.getId(), targetId)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 将分类实体转换为DTO
     * @param category 分类实体
     * @return 分类DTO
     */
    private JobCategoryDTO convertToDTO(JobCategory category) {
        JobCategoryDTO dto = new JobCategoryDTO();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }
} 
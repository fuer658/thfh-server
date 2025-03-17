package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.JobCategoryDTO;
import com.thfh.service.JobCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 职位分类控制器
 * 提供职位分类的增删改查等功能
 */
@RestController
@RequestMapping("/api/job-categories")
public class JobCategoryController {
    @Autowired
    private JobCategoryService jobCategoryService;

    /**
     * 获取所有职位分类（树形结构）
     * @return 职位分类树形列表
     */
    @GetMapping
    public Result<List<JobCategoryDTO>> getAllCategories() {
        return Result.success(jobCategoryService.getAllCategoriesTree());
    }

    /**
     * 获取所有启用的职位分类（树形结构）
     * @return 启用的职位分类树形列表
     */
    @GetMapping("/enabled")
    public Result<List<JobCategoryDTO>> getEnabledCategories() {
        return Result.success(jobCategoryService.getEnabledCategoriesTree());
    }

    /**
     * 根据ID获取职位分类
     * @param id 分类ID
     * @return 职位分类信息
     */
    @GetMapping("/{id}")
    public Result<JobCategoryDTO> getCategoryById(@PathVariable Long id) {
        return Result.success(jobCategoryService.getCategoryById(id));
    }

    /**
     * 创建职位分类
     * @param categoryDTO 职位分类信息
     * @return 创建后的职位分类信息
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<JobCategoryDTO> createCategory(@RequestBody JobCategoryDTO categoryDTO) {
        return Result.success(jobCategoryService.createCategory(categoryDTO));
    }

    /**
     * 更新职位分类
     * @param id 分类ID
     * @param categoryDTO 更新的职位分类信息
     * @return 更新后的职位分类信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<JobCategoryDTO> updateCategory(@PathVariable Long id, @RequestBody JobCategoryDTO categoryDTO) {
        return Result.success(jobCategoryService.updateCategory(id, categoryDTO));
    }

    /**
     * 删除职位分类
     * @param id 分类ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        jobCategoryService.deleteCategory(id);
        return Result.success(null);
    }

    /**
     * 切换职位分类启用状态
     * @param id 分类ID
     * @return 更新后的职位分类信息
     */
    @PutMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<JobCategoryDTO> toggleCategoryStatus(@PathVariable Long id) {
        return Result.success(jobCategoryService.toggleCategoryStatus(id));
    }
} 
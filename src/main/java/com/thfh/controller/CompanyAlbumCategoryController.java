package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.CompanyAlbumCategoryDTO;
import com.thfh.service.CompanyAlbumCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company-album-categories")
public class CompanyAlbumCategoryController {
    @Autowired
    private CompanyAlbumCategoryService categoryService;

    /**
     * 创建相册分类
     * @param categoryDTO 分类信息
     * @return 创建的分类信息
     */
    @PostMapping
    public Result<CompanyAlbumCategoryDTO> createCategory(@RequestBody CompanyAlbumCategoryDTO categoryDTO) {
        return Result.success(categoryService.createCategory(categoryDTO));
    }

    /**
     * 更新相册分类
     * @param id 分类ID
     * @param categoryDTO 更新的分类信息
     * @return 更新后的分类信息
     */
    @PutMapping("/{id}")
    public Result<CompanyAlbumCategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CompanyAlbumCategoryDTO categoryDTO) {
        return Result.success(categoryService.updateCategory(id, categoryDTO));
    }

    /**
     * 删除相册分类
     * @param id 分类ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success(null);
    }

    /**
     * 获取相册分类列表
     * @param companyId 公司ID
     * @return 分类列表
     */
    @GetMapping("/company/{companyId}")
    public Result<List<CompanyAlbumCategoryDTO>> getCategories(@PathVariable Long companyId) {
        return Result.success(categoryService.getCategories(companyId));
    }

    /**
     * 分页查询相册分类
     * @param companyId 公司ID
     * @param name 分类名称（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页的分类列表
     */
    @GetMapping("/company/{companyId}/page")
    public Result<Page<CompanyAlbumCategoryDTO>> getCategoriesPage(
            @PathVariable Long companyId,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(categoryService.getCategoriesPage(companyId, name, pageNum, pageSize));
    }
} 
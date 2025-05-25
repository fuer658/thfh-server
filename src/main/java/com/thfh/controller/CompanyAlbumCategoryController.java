package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.CompanyAlbumCategoryDTO;
import com.thfh.service.CompanyAlbumCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 企业相册分类管理控制器
 * 提供企业相册分类的增删改查等功能
 */
@Tag(name = "企业相册分类管理", description = "企业相册分类相关的API接口，包括分类的增删改查等功能")
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
    @Operation(summary = "创建相册分类", description = "创建一个新的企业相册分类，需要提供分类的基本信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @PostMapping
    public Result<CompanyAlbumCategoryDTO> createCategory(
            @Parameter(description = "分类信息", required = true) @RequestBody CompanyAlbumCategoryDTO categoryDTO) {
        return Result.success(categoryService.createCategory(categoryDTO));
    }

    /**
     * 更新相册分类
     * @param id 分类ID
     * @param categoryDTO 更新的分类信息
     * @return 更新后的分类信息
     */
    @Operation(summary = "更新相册分类", description = "根据分类ID更新分类信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @PutMapping("/{id}")
    public Result<CompanyAlbumCategoryDTO> updateCategory(
            @Parameter(description = "分类ID", required = true) @PathVariable Long id,
            @Parameter(description = "更新的分类信息", required = true) @RequestBody CompanyAlbumCategoryDTO categoryDTO) {
        return Result.success(categoryService.updateCategory(id, categoryDTO));
    }

    /**
     * 删除相册分类
     * @param id 分类ID
     * @return 操作结果
     */
    @Operation(summary = "删除相册分类", description = "根据分类ID删除分类，如果分类下有相册会连同相册一起删除")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类ID", required = true) @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success(null);
    }

    /**
     * 获取相册分类列表
     * @param companyId 公司ID
     * @return 分类列表
     */
    @Operation(summary = "获取相册分类列表", description = "获取指定公司的所有相册分类列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/company/{companyId}")
    public Result<List<CompanyAlbumCategoryDTO>> getCategories(
            @Parameter(description = "公司ID", required = true) @PathVariable Long companyId) {
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
    @Operation(summary = "分页查询相册分类", description = "分页获取指定公司的相册分类列表，支持按名称筛选")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/company/{companyId}/page")
    public Result<Page<CompanyAlbumCategoryDTO>> getCategoriesPage(
            @Parameter(description = "公司ID", required = true) @PathVariable Long companyId,
            @Parameter(description = "分类名称（可选）") @RequestParam(required = false) String name,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(categoryService.getCategoriesPage(companyId, name, pageNum, pageSize));
    }
} 
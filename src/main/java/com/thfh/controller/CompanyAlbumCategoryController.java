package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.CompanyAlbumCategoryDTO;
import com.thfh.service.CompanyAlbumCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 企业相册分类管理控制器
 * 提供企业相册分类的增删改查等功能
 */
@Api(tags = "企业相册分类管理", description = "企业相册分类相关的API接口，包括分类的增删改查等功能")
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
    @ApiOperation(value = "创建相册分类", notes = "创建一个新的企业相册分类，需要提供分类的基本信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "创建成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @PostMapping
    public Result<CompanyAlbumCategoryDTO> createCategory(
            @ApiParam(value = "分类信息", required = true) @RequestBody CompanyAlbumCategoryDTO categoryDTO) {
        return Result.success(categoryService.createCategory(categoryDTO));
    }

    /**
     * 更新相册分类
     * @param id 分类ID
     * @param categoryDTO 更新的分类信息
     * @return 更新后的分类信息
     */
    @ApiOperation(value = "更新相册分类", notes = "根据分类ID更新分类信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "分类不存在")
    })
    @PutMapping("/{id}")
    public Result<CompanyAlbumCategoryDTO> updateCategory(
            @ApiParam(value = "分类ID", required = true) @PathVariable Long id,
            @ApiParam(value = "更新的分类信息", required = true) @RequestBody CompanyAlbumCategoryDTO categoryDTO) {
        return Result.success(categoryService.updateCategory(id, categoryDTO));
    }

    /**
     * 删除相册分类
     * @param id 分类ID
     * @return 操作结果
     */
    @ApiOperation(value = "删除相册分类", notes = "根据分类ID删除分类，如果分类下有相册会连同相册一起删除")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "分类不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(
            @ApiParam(value = "分类ID", required = true) @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success(null);
    }

    /**
     * 获取相册分类列表
     * @param companyId 公司ID
     * @return 分类列表
     */
    @ApiOperation(value = "获取相册分类列表", notes = "获取指定公司的所有相册分类列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/company/{companyId}")
    public Result<List<CompanyAlbumCategoryDTO>> getCategories(
            @ApiParam(value = "公司ID", required = true) @PathVariable Long companyId) {
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
    @ApiOperation(value = "分页查询相册分类", notes = "分页获取指定公司的相册分类列表，支持按名称筛选")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/company/{companyId}/page")
    public Result<Page<CompanyAlbumCategoryDTO>> getCategoriesPage(
            @ApiParam(value = "公司ID", required = true) @PathVariable Long companyId,
            @ApiParam(value = "分类名称（可选）") @RequestParam(required = false) String name,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(categoryService.getCategoriesPage(companyId, name, pageNum, pageSize));
    }
} 
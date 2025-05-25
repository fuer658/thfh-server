package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.JobCategoryDTO;
import com.thfh.service.JobCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 职位分类控制器
 * 提供职位分类的增删改查等功能
 */
@Tag(name = "职位分类管理", description = "提供职位分类的增删改查等功能")
@RestController
@RequestMapping("/api/job-categories")
public class JobCategoryController {
    @Autowired
    private JobCategoryService jobCategoryService;

    /**
     * 获取所有职位分类（树形结构）
     * @return 职位分类树形列表
     */
    @Operation(summary = "获取所有职位分类", description = "获取所有职位分类列表，以树形结构返回")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping
    public Result<List<JobCategoryDTO>> getAllCategories() {
        return Result.success(jobCategoryService.getAllCategoriesTree());
    }

    /**
     * 获取所有启用的职位分类（树形结构）
     * @return 启用的职位分类树形列表
     */
    @Operation(summary = "获取所有启用的职位分类", description = "获取所有启用状态的职位分类列表，以树形结构返回")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/enabled")
    public Result<List<JobCategoryDTO>> getEnabledCategories() {
        return Result.success(jobCategoryService.getEnabledCategoriesTree());
    }

    /**
     * 根据ID获取职位分类
     * @param id 分类ID
     * @return 职位分类信息
     */
    @Operation(summary = "根据ID获取职位分类", description = "通过分类ID查询职位分类的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @GetMapping("/{id}")
    public Result<JobCategoryDTO> getCategoryById(
            @Parameter(description = "分类ID", required = true) @PathVariable Long id) {
        return Result.success(jobCategoryService.getCategoryById(id));
    }

    /**
     * 创建职位分类
     * @param categoryDTO 职位分类信息
     * @return 创建后的职位分类信息
     */
    @Operation(summary = "创建职位分类", description = "创建一个新的职位分类，需要管理员权限")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有管理员权限")
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<JobCategoryDTO> createCategory(
            @Parameter(description = "职位分类信息", required = true) @RequestBody JobCategoryDTO categoryDTO) {
        return Result.success(jobCategoryService.createCategory(categoryDTO));
    }

    /**
     * 更新职位分类
     * @param id 分类ID
     * @param categoryDTO 更新的职位分类信息
     * @return 更新后的职位分类信息
     */
    @Operation(summary = "更新职位分类", description = "根据分类ID更新职位分类信息，需要管理员权限")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有管理员权限"),
        @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<JobCategoryDTO> updateCategory(
            @Parameter(description = "分类ID", required = true) @PathVariable Long id, 
            @Parameter(description = "更新的职位分类信息", required = true) @RequestBody JobCategoryDTO categoryDTO) {
        return Result.success(jobCategoryService.updateCategory(id, categoryDTO));
    }

    /**
     * 删除职位分类
     * @param id 分类ID
     * @return 操作结果
     */
    @Operation(summary = "删除职位分类", description = "根据分类ID删除职位分类，需要管理员权限")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有管理员权限"),
        @ApiResponse(responseCode = "404", description = "分类不存在"),
        @ApiResponse(responseCode = "400", description = "分类下有子分类或职位，不能删除")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类ID", required = true) @PathVariable Long id) {
        jobCategoryService.deleteCategory(id);
        return Result.success(null);
    }

    /**
     * 切换职位分类启用状态
     * @param id 分类ID
     * @return 更新后的职位分类信息
     */
    @Operation(summary = "切换职位分类启用状态", description = "启用或禁用职位分类，需要管理员权限")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "操作成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有管理员权限"),
        @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @PutMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<JobCategoryDTO> toggleCategoryStatus(
            @Parameter(description = "分类ID", required = true) @PathVariable Long id) {
        return Result.success(jobCategoryService.toggleCategoryStatus(id));
    }

    /**
     * 根据父分类ID获取子分类列表
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @Operation(summary = "获取子分类列表", description = "根据父分类ID获取直接子分类列表，parentId为空时获取顶级分类")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/children")
    public Result<List<JobCategoryDTO>> getChildCategories(
            @Parameter(description = "父分类ID，为空时获取顶级分类") @RequestParam(required = false) Long parentId) {
        return Result.success(jobCategoryService.getChildCategories(parentId));
    }
} 
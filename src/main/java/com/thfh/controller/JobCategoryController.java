package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.JobCategoryDTO;
import com.thfh.service.JobCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 职位分类控制器
 * 提供职位分类的增删改查等功能
 */
@Api(tags = "职位分类管理", description = "提供职位分类的增删改查等功能")
@RestController
@RequestMapping("/api/job-categories")
public class JobCategoryController {
    @Autowired
    private JobCategoryService jobCategoryService;

    /**
     * 获取所有职位分类（树形结构）
     * @return 职位分类树形列表
     */
    @ApiOperation(value = "获取所有职位分类", notes = "获取所有职位分类列表，以树形结构返回")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping
    public Result<List<JobCategoryDTO>> getAllCategories() {
        return Result.success(jobCategoryService.getAllCategoriesTree());
    }

    /**
     * 获取所有启用的职位分类（树形结构）
     * @return 启用的职位分类树形列表
     */
    @ApiOperation(value = "获取所有启用的职位分类", notes = "获取所有启用状态的职位分类列表，以树形结构返回")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
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
    @ApiOperation(value = "根据ID获取职位分类", notes = "通过分类ID查询职位分类的详细信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "分类不存在")
    })
    @GetMapping("/{id}")
    public Result<JobCategoryDTO> getCategoryById(
            @ApiParam(value = "分类ID", required = true) @PathVariable Long id) {
        return Result.success(jobCategoryService.getCategoryById(id));
    }

    /**
     * 创建职位分类
     * @param categoryDTO 职位分类信息
     * @return 创建后的职位分类信息
     */
    @ApiOperation(value = "创建职位分类", notes = "创建一个新的职位分类，需要管理员权限")
    @ApiResponses({
        @ApiResponse(code = 200, message = "创建成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有管理员权限")
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<JobCategoryDTO> createCategory(
            @ApiParam(value = "职位分类信息", required = true) @RequestBody JobCategoryDTO categoryDTO) {
        return Result.success(jobCategoryService.createCategory(categoryDTO));
    }

    /**
     * 更新职位分类
     * @param id 分类ID
     * @param categoryDTO 更新的职位分类信息
     * @return 更新后的职位分类信息
     */
    @ApiOperation(value = "更新职位分类", notes = "根据分类ID更新职位分类信息，需要管理员权限")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有管理员权限"),
        @ApiResponse(code = 404, message = "分类不存在")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<JobCategoryDTO> updateCategory(
            @ApiParam(value = "分类ID", required = true) @PathVariable Long id, 
            @ApiParam(value = "更新的职位分类信息", required = true) @RequestBody JobCategoryDTO categoryDTO) {
        return Result.success(jobCategoryService.updateCategory(id, categoryDTO));
    }

    /**
     * 删除职位分类
     * @param id 分类ID
     * @return 操作结果
     */
    @ApiOperation(value = "删除职位分类", notes = "根据分类ID删除职位分类，需要管理员权限")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有管理员权限"),
        @ApiResponse(code = 404, message = "分类不存在"),
        @ApiResponse(code = 400, message = "分类下有子分类或职位，不能删除")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> deleteCategory(
            @ApiParam(value = "分类ID", required = true) @PathVariable Long id) {
        jobCategoryService.deleteCategory(id);
        return Result.success(null);
    }

    /**
     * 切换职位分类启用状态
     * @param id 分类ID
     * @return 更新后的职位分类信息
     */
    @ApiOperation(value = "切换职位分类启用状态", notes = "启用或禁用职位分类，需要管理员权限")
    @ApiResponses({
        @ApiResponse(code = 200, message = "操作成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有管理员权限"),
        @ApiResponse(code = 404, message = "分类不存在")
    })
    @PutMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<JobCategoryDTO> toggleCategoryStatus(
            @ApiParam(value = "分类ID", required = true) @PathVariable Long id) {
        return Result.success(jobCategoryService.toggleCategoryStatus(id));
    }

    /**
     * 根据父分类ID获取子分类列表
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @ApiOperation(value = "获取子分类列表", notes = "根据父分类ID获取直接子分类列表，parentId为空时获取顶级分类")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/children")
    public Result<List<JobCategoryDTO>> getChildCategories(
            @ApiParam(value = "父分类ID，为空时获取顶级分类") @RequestParam(required = false) Long parentId) {
        return Result.success(jobCategoryService.getChildCategories(parentId));
    }
} 
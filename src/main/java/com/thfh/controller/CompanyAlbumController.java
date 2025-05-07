package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.CompanyAlbumDTO;
import com.thfh.service.CompanyAlbumService;
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
 * 企业相册管理控制器
 * 提供企业相册的增删改查等功能
 */
@Api(tags = "企业相册管理", description = "企业相册相关的API接口，包括相册的增删改查等功能")
@RestController
@RequestMapping("/api/company-albums")
public class CompanyAlbumController {
    @Autowired
    private CompanyAlbumService albumService;

    /**
     * 创建相册
     * @param albumDTO 相册信息
     * @return 创建的相册信息
     */
    @ApiOperation(value = "创建相册", notes = "创建一个新的企业相册，需要提供相册的基本信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "创建成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @PostMapping
    public Result<CompanyAlbumDTO> createAlbum(
            @ApiParam(value = "相册信息", required = true) @RequestBody CompanyAlbumDTO albumDTO) {
        return Result.success(albumService.createAlbum(albumDTO));
    }

    /**
     * 更新相册
     * @param id 相册ID
     * @param albumDTO 更新的相册信息
     * @return 更新后的相册信息
     */
    @ApiOperation(value = "更新相册", notes = "根据相册ID更新相册信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "相册不存在")
    })
    @PutMapping("/{id}")
    public Result<CompanyAlbumDTO> updateAlbum(
            @ApiParam(value = "相册ID", required = true) @PathVariable Long id,
            @ApiParam(value = "更新的相册信息", required = true) @RequestBody CompanyAlbumDTO albumDTO) {
        return Result.success(albumService.updateAlbum(id, albumDTO));
    }

    /**
     * 删除相册
     * @param id 相册ID
     * @return 操作结果
     */
    @ApiOperation(value = "删除相册", notes = "根据相册ID删除相册")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "相册不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> deleteAlbum(
            @ApiParam(value = "相册ID", required = true) @PathVariable Long id) {
        albumService.deleteAlbum(id);
        return Result.success(null);
    }

    /**
     * 获取相册列表
     * @param companyId 公司ID
     * @return 相册列表
     */
    @ApiOperation(value = "获取相册列表", notes = "获取指定公司的所有相册列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/company/{companyId}")
    public Result<List<CompanyAlbumDTO>> getAlbums(
            @ApiParam(value = "公司ID", required = true) @PathVariable Long companyId) {
        return Result.success(albumService.getAlbums(companyId));
    }

    /**
     * 获取分类下的相册列表
     * @param categoryId 分类ID
     * @return 相册列表
     */
    @ApiOperation(value = "获取分类下的相册列表", notes = "获取指定分类下的所有相册列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "分类不存在")
    })
    @GetMapping("/category/{categoryId}")
    public Result<List<CompanyAlbumDTO>> getAlbumsByCategory(
            @ApiParam(value = "分类ID", required = true) @PathVariable Long categoryId) {
        return Result.success(albumService.getAlbumsByCategory(categoryId));
    }

    /**
     * 分页查询相册
     * @param companyId 公司ID
     * @param title 标题（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页的相册列表
     */
    @ApiOperation(value = "分页查询相册", notes = "分页获取指定公司的相册列表，支持按标题筛选")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/company/{companyId}/page")
    public Result<Page<CompanyAlbumDTO>> getAlbumsPage(
            @ApiParam(value = "公司ID", required = true) @PathVariable Long companyId,
            @ApiParam(value = "标题（可选）") @RequestParam(required = false) String title,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(albumService.getAlbumsPage(companyId, title, pageNum, pageSize));
    }

    /**
     * 分页查询分类下的相册
     * @param categoryId 分类ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页的相册列表
     */
    @ApiOperation(value = "分页查询分类下的相册", notes = "分页获取指定分类下的相册列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "分类不存在")
    })
    @GetMapping("/category/{categoryId}/page")
    public Result<Page<CompanyAlbumDTO>> getAlbumsPageByCategory(
            @ApiParam(value = "分类ID", required = true) @PathVariable Long categoryId,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(albumService.getAlbumsPageByCategory(categoryId, pageNum, pageSize));
    }
} 
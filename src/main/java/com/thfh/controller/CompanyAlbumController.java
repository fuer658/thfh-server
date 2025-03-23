package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.CompanyAlbumDTO;
import com.thfh.service.CompanyAlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping
    public Result<CompanyAlbumDTO> createAlbum(@RequestBody CompanyAlbumDTO albumDTO) {
        return Result.success(albumService.createAlbum(albumDTO));
    }

    /**
     * 更新相册
     * @param id 相册ID
     * @param albumDTO 更新的相册信息
     * @return 更新后的相册信息
     */
    @PutMapping("/{id}")
    public Result<CompanyAlbumDTO> updateAlbum(@PathVariable Long id, @RequestBody CompanyAlbumDTO albumDTO) {
        return Result.success(albumService.updateAlbum(id, albumDTO));
    }

    /**
     * 删除相册
     * @param id 相册ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
        return Result.success(null);
    }

    /**
     * 获取相册列表
     * @param companyId 公司ID
     * @return 相册列表
     */
    @GetMapping("/company/{companyId}")
    public Result<List<CompanyAlbumDTO>> getAlbums(@PathVariable Long companyId) {
        return Result.success(albumService.getAlbums(companyId));
    }

    /**
     * 获取分类下的相册列表
     * @param categoryId 分类ID
     * @return 相册列表
     */
    @GetMapping("/category/{categoryId}")
    public Result<List<CompanyAlbumDTO>> getAlbumsByCategory(@PathVariable Long categoryId) {
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
    @GetMapping("/company/{companyId}/page")
    public Result<Page<CompanyAlbumDTO>> getAlbumsPage(
            @PathVariable Long companyId,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(albumService.getAlbumsPage(companyId, title, pageNum, pageSize));
    }

    /**
     * 分页查询分类下的相册
     * @param categoryId 分类ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页的相册列表
     */
    @GetMapping("/category/{categoryId}/page")
    public Result<Page<CompanyAlbumDTO>> getAlbumsPageByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(albumService.getAlbumsPageByCategory(categoryId, pageNum, pageSize));
    }
} 
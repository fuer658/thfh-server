package com.thfh.controller;

import com.thfh.dto.ArtworkGalleryDTO;
import com.thfh.service.ArtworkGalleryService;
import com.thfh.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 作品图册控制器
 */
@Api(tags = "作品图册接口")
@RestController
@RequestMapping("/api/artworks/{artworkId}/gallery")
public class ArtworkGalleryController {

    @Autowired
    private ArtworkGalleryService artworkGalleryService;

    /**
     * 添加图册图片
     */
    @ApiOperation("添加图册图片")
    @ApiResponses({
        @ApiResponse(code = 200, message = "添加成功"),
        @ApiResponse(code = 401, message = "未授权"),
        @ApiResponse(code = 404, message = "作品不存在")
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<ArtworkGalleryDTO> addGalleryImage(
            @ApiParam("作品ID") @PathVariable Long artworkId,
            @ApiParam("图片文件") @RequestParam("file") MultipartFile file,
            @ApiParam("图片描述") @RequestParam(value = "description", required = false) String description) {
        ArtworkGalleryDTO gallery = artworkGalleryService.addGalleryImage(artworkId, file, description);
        return Result.success(gallery);
    }

    /**
     * 获取图册列表
     */
    @ApiOperation("获取图册列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "作品不存在")
    })
    @GetMapping
    public Result<List<ArtworkGalleryDTO>> getGalleryList(
            @ApiParam("作品ID") @PathVariable Long artworkId) {
        List<ArtworkGalleryDTO> galleries = artworkGalleryService.getGalleryByArtworkId(artworkId);
        return Result.success(galleries);
    }

    /**
     * 删除图册图片
     */
    @ApiOperation("删除图册图片")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权"),
        @ApiResponse(code = 404, message = "图册不存在")
    })
    @DeleteMapping("/{galleryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> deleteGalleryImage(
            @ApiParam("作品ID") @PathVariable Long artworkId,
            @ApiParam("图册ID") @PathVariable Long galleryId) {
        artworkGalleryService.deleteGalleryImage(galleryId);
        return Result.success();
    }

    /**
     * 更新图册图片排序
     */
    @ApiOperation("更新图册图片排序")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 401, message = "未授权"),
        @ApiResponse(code = 404, message = "图册不存在")
    })
    @PutMapping("/{galleryId}/sort")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> updateGallerySortOrder(
            @ApiParam("作品ID") @PathVariable Long artworkId,
            @ApiParam("图册ID") @PathVariable Long galleryId,
            @ApiParam("新的排序序号") @RequestParam Integer newSortOrder) {
        artworkGalleryService.updateGallerySortOrder(galleryId, newSortOrder);
        return Result.success();
    }
} 
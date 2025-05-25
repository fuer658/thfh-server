package com.thfh.controller;

import com.thfh.dto.ArtworkGalleryDTO;
import com.thfh.service.ArtworkGalleryService;
import com.thfh.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 作品图册控制器
 */
@Tag(name = "作品图册接口")
@RestController
@RequestMapping("/api/artworks/{artworkId}/gallery")
public class ArtworkGalleryController {

    @Autowired
    private ArtworkGalleryService artworkGalleryService;

    /**
     * 添加图册图片
     */
    @Operation(summary = "添加图册图片")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "添加成功"),
        @ApiResponse(responseCode = "401", description = "未授权"),
        @ApiResponse(responseCode = "404", description = "作品不存在")
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<ArtworkGalleryDTO> addGalleryImage(
            @Parameter(description = "作品ID") @PathVariable Long artworkId,
            @Parameter(description = "图片文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "图片描述") @RequestParam(value = "description", required = false) String description) {
        ArtworkGalleryDTO gallery = artworkGalleryService.addGalleryImage(artworkId, file, description);
        return Result.success(gallery);
    }

    /**
     * 获取图册列表
     */
    @Operation(summary = "获取图册列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "作品不存在")
    })
    @GetMapping
    public Result<List<ArtworkGalleryDTO>> getGalleryList(
            @Parameter(description = "作品ID") @PathVariable Long artworkId) {
        List<ArtworkGalleryDTO> galleries = artworkGalleryService.getGalleryByArtworkId(artworkId);
        return Result.success(galleries);
    }

    /**
     * 删除图册图片
     */
    @Operation(summary = "删除图册图片")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权"),
        @ApiResponse(responseCode = "404", description = "图册不存在")
    })
    @DeleteMapping("/{galleryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> deleteGalleryImage(
            @Parameter(description = "作品ID") @PathVariable Long artworkId,
            @Parameter(description = "图册ID") @PathVariable Long galleryId) {
        artworkGalleryService.deleteGalleryImage(galleryId);
        return Result.success();
    }

    /**
     * 更新图册图片排序
     */
    @Operation(summary = "更新图册图片排序")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "401", description = "未授权"),
        @ApiResponse(responseCode = "404", description = "图册不存在")
    })
    @PutMapping("/{galleryId}/sort")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> updateGallerySortOrder(
            @Parameter(description = "作品ID") @PathVariable Long artworkId,
            @Parameter(description = "图册ID") @PathVariable Long galleryId,
            @Parameter(description = "新的排序序号") @RequestParam Integer newSortOrder) {
        artworkGalleryService.updateGallerySortOrder(galleryId, newSortOrder);
        return Result.success();
    }
} 
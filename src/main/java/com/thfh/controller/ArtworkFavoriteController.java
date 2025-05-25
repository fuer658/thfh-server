package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.model.Artwork;
import com.thfh.model.User;
import com.thfh.service.ArtworkFavoriteService;
import com.thfh.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 作品收藏控制器
 * 提供作品收藏相关的API接口
 */
@Tag(name = "作品收藏", description = "作品收藏相关的API接口")
@RestController
@RequestMapping("/api/artworks")
public class ArtworkFavoriteController {

    @Autowired
    private ArtworkFavoriteService artworkFavoriteService;

    @Autowired
    private UserService userService;

    /**
     * 添加收藏
     * @param artworkId 作品ID
     * @param authentication 认证信息
     * @return 操作结果
     */
    @Operation(summary = "添加收藏", description = "为指定作品添加收藏")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "收藏成功"),
            @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
            @ApiResponse(responseCode = "404", description = "作品不存在")
    })
    @PostMapping("/{artworkId}/favorite")
    public Result<Void> addFavorite(
            @Parameter(description = "作品ID", required = true) @PathVariable Long artworkId,
            @Parameter(hidden = true) Authentication authentication) {
        User user = userService.getCurrentUser();
        artworkFavoriteService.addFavorite(artworkId, user);
        return Result.success(null);
    }

    /**
     * 取消收藏
     * @param artworkId 作品ID
     * @param authentication 认证信息
     * @return 操作结果
     */
    @Operation(summary = "取消收藏", description = "取消对指定作品的收藏")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "取消收藏成功"),
            @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
            @ApiResponse(responseCode = "404", description = "作品不存在")
    })
    @DeleteMapping("/{artworkId}/favorite")
    public Result<Void> removeFavorite(
            @Parameter(description = "作品ID", required = true) @PathVariable Long artworkId,
            @Parameter(hidden = true) Authentication authentication) {
        User user = userService.getCurrentUser();
        artworkFavoriteService.removeFavorite(artworkId, user);
        return Result.success(null);
    }

    /**
     * 获取用户收藏的作品列表
     * @param authentication 认证信息
     * @param page 页码
     * @param size 每页大小
     * @return 收藏的作品分页列表
     */
    @Operation(summary = "获取用户收藏的作品列表", description = "获取当前用户收藏的作品分页列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/favorites")
    public Result<Page<Artwork>> getFavorites(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        User user = userService.getCurrentUser();
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Artwork> artworkPage = artworkFavoriteService.getUserFavorites(user.getId(), pageRequest);
        return Result.success(artworkPage);
    }

    /**
     * 检查作品是否已收藏
     * @param artworkId 作品ID
     * @param authentication 认证信息
     * @return 是否已收藏
     */
    @Operation(summary = "检查作品是否已收藏", description = "检查当前用户是否已收藏指定作品")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
            @ApiResponse(responseCode = "404", description = "作品不存在")
    })
    @GetMapping("/{artworkId}/favorite/check")
    public Result<Boolean> checkFavorite(
            @Parameter(description = "作品ID", required = true) @PathVariable Long artworkId,
            @Parameter(hidden = true) Authentication authentication) {
        User user = userService.getCurrentUser();
        return Result.success(artworkFavoriteService.isFavorited(artworkId, user.getId()));
    }
}
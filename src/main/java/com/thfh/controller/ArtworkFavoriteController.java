package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.model.Artwork;
import com.thfh.model.User;
import com.thfh.service.ArtworkFavoriteService;
import com.thfh.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
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
@Api(tags = "作品收藏", description = "作品收藏相关的API接口")
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
     * @param user 当前登录用户
     * @return 操作结果
     */
    @ApiOperation(value = "添加收藏", notes = "为指定作品添加收藏")
    @ApiResponses({
            @ApiResponse(code = 200, message = "收藏成功"),
            @ApiResponse(code = 401, message = "未授权，请先登录"),
            @ApiResponse(code = 404, message = "作品不存在")
    })
    @PostMapping("/{artworkId}/favorite")
    public Result<Void> addFavorite(
            @ApiParam(value = "作品ID", required = true) @PathVariable Long artworkId,
            @ApiParam(hidden = true) Authentication authentication) {
        User user = userService.getCurrentUser();
        artworkFavoriteService.addFavorite(artworkId, user);
        return Result.success(null);
    }

    /**
     * 取消收藏
     * @param artworkId 作品ID
     * @param user 当前登录用户
     * @return 操作结果
     */
    @ApiOperation(value = "取消收藏", notes = "取消对指定作品的收藏")
    @ApiResponses({
            @ApiResponse(code = 200, message = "取消收藏成功"),
            @ApiResponse(code = 401, message = "未授权，请先登录"),
            @ApiResponse(code = 404, message = "作品不存在")
    })
    @DeleteMapping("/{artworkId}/favorite")
    public Result<Void> removeFavorite(
            @ApiParam(value = "作品ID", required = true) @PathVariable Long artworkId,
            @ApiParam(hidden = true) Authentication authentication) {
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
    @ApiOperation(value = "获取用户收藏的作品列表", notes = "获取当前用户收藏的作品分页列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功"),
            @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/favorites")
    public Result<Page<Artwork>> getFavorites(
            @ApiParam(hidden = true) Authentication authentication,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        User user = userService.getCurrentUser();
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Artwork> artworkPage = artworkFavoriteService.getUserFavorites(user.getId(), pageRequest);
        return Result.success(artworkPage);
    }

    /**
     * 检查作品是否已收藏
     * @param artworkId 作品ID
     * @param user 当前登录用户
     * @return 是否已收藏
     */
    @ApiOperation(value = "检查作品是否已收藏", notes = "检查当前用户是否已收藏指定作品")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功"),
            @ApiResponse(code = 401, message = "未授权，请先登录"),
            @ApiResponse(code = 404, message = "作品不存在")
    })
    @GetMapping("/{artworkId}/favorite/check")
    public Result<Boolean> checkFavorite(
            @ApiParam(value = "作品ID", required = true) @PathVariable Long artworkId,
            @ApiParam(hidden = true) Authentication authentication) {
        User user = userService.getCurrentUser();
        return Result.success(artworkFavoriteService.isFavorited(artworkId, user.getId()));
    }
}
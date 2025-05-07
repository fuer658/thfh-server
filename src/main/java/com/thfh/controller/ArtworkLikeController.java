package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.model.Artwork;
import com.thfh.model.User;
import com.thfh.service.ArtworkLikeService;
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
 * 作品点赞控制器
 * 提供作品点赞相关的API接口
 */
@Api(tags = "作品点赞", description = "作品点赞相关的API接口")
@RestController
@RequestMapping("/api/artworks")
public class ArtworkLikeController {

    @Autowired
    private ArtworkLikeService artworkLikeService;

    @Autowired
    private UserService userService;

    /**
     * 添加点赞
     * @param artworkId 作品ID
     * @param authentication 认证信息
     * @return 操作结果
     */
    @ApiOperation(value = "添加点赞", notes = "为指定作品添加点赞")
    @ApiResponses({
            @ApiResponse(code = 200, message = "点赞成功"),
            @ApiResponse(code = 401, message = "未授权，请先登录"),
            @ApiResponse(code = 404, message = "作品不存在")
    })
    @PostMapping("/{artworkId}/like")
    public Result<Void> addLike(
            @ApiParam(value = "作品ID", required = true) @PathVariable Long artworkId,
            @ApiParam(hidden = true) Authentication authentication) {
        User user = userService.getCurrentUser();
        artworkLikeService.addLike(artworkId, user);
        return Result.success(null);
    }

    /**
     * 取消点赞
     * @param artworkId 作品ID
     * @param authentication 认证信息
     * @return 操作结果
     */
    @ApiOperation(value = "取消点赞", notes = "取消对指定作品的点赞")
    @ApiResponses({
            @ApiResponse(code = 200, message = "取消点赞成功"),
            @ApiResponse(code = 401, message = "未授权，请先登录"),
            @ApiResponse(code = 404, message = "作品不存在")
    })
    @DeleteMapping("/{artworkId}/like")
    public Result<Void> removeLike(
            @ApiParam(value = "作品ID", required = true) @PathVariable Long artworkId,
            @ApiParam(hidden = true) Authentication authentication) {
        User user = userService.getCurrentUser();
        artworkLikeService.removeLike(artworkId, user);
        return Result.success(null);
    }

    /**
     * 获取用户点赞的作品列表
     * @param authentication 认证信息
     * @param page 页码
     * @param size 每页大小
     * @return 点赞的作品分页列表
     */
    @ApiOperation(value = "获取用户点赞的作品列表", notes = "获取当前用户点赞的作品分页列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功"),
            @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/likes")
    public Result<Page<Artwork>> getLikes(
            @ApiParam(hidden = true) Authentication authentication,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        User user = userService.getCurrentUser();
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "likeTime"));
        Page<Artwork> artworkPage = artworkLikeService.getUserLikes(user.getId(), pageRequest);
        return Result.success(artworkPage);
    }

    /**
     * 检查作品是否已点赞
     * @param artworkId 作品ID
     * @param authentication 认证信息
     * @return 是否已点赞
     */
    @ApiOperation(value = "检查作品是否已点赞", notes = "检查当前用户是否已点赞指定作品")
    @ApiResponses({
            @ApiResponse(code = 200, message = "获取成功"),
            @ApiResponse(code = 401, message = "未授权，请先登录"),
            @ApiResponse(code = 404, message = "作品不存在")
    })
    @GetMapping("/{artworkId}/like/check")
    public Result<Boolean> checkLike(
            @ApiParam(value = "作品ID", required = true) @PathVariable Long artworkId,
            @ApiParam(hidden = true) Authentication authentication) {
        User user = userService.getCurrentUser();
        return Result.success(artworkLikeService.isLiked(artworkId, user.getId()));
    }
}
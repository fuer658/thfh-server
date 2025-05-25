package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.model.Artwork;
import com.thfh.model.User;
import com.thfh.service.ArtworkLikeService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 作品点赞控制器
 * 提供作品点赞相关的API接口
 */
@Tag(name = "作品点赞", description = "作品点赞相关的API接口")
@RestController
@RequestMapping("/api/artworks")
public class ArtworkLikeController {

    @Autowired
    private ArtworkLikeService artworkLikeService;

    @Autowired
    private UserService userService;

    /**
     * 添加点赞
     * 幂等性：重复点赞不会报错，只会记录一次
     * 注意：artworkId需为正数，且作品必须存在
     * @param artworkId 作品ID
     * @param authentication 认证信息
     * @return 操作结果
     */
    @PreAuthorize("hasRole('USER') or hasRole('ROLE_USER')")
    @Operation(summary = "添加点赞", description = "为指定作品添加点赞")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "点赞成功"),
            @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
            @ApiResponse(responseCode = "404", description = "作品不存在")
    })
    @PostMapping("/{artworkId}/like")
    public Result<Void> addLike(
            @Parameter(description = "作品ID", required = true) @PathVariable Long artworkId,
            @Parameter(hidden = true) Authentication authentication) {
        if (artworkId == null || artworkId <= 0) {
            return Result.error("参数错误：artworkId非法");
        }
        User user = userService.getCurrentUser();
        if (user == null) {
            return Result.unauthorized("未登录");
        }
        artworkLikeService.addLike(artworkId, user);
        return Result.success(null);
    }

    /**
     * 取消点赞
     * 幂等性：未点赞时取消不会报错
     * @param artworkId 作品ID
     * @param authentication 认证信息
     * @return 操作结果
     */
    @PreAuthorize("hasRole('USER') or hasRole('ROLE_USER')")
    @Operation(summary = "取消点赞", description = "取消对指定作品的点赞")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "取消点赞成功"),
            @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
            @ApiResponse(responseCode = "404", description = "作品不存在")
    })
    @DeleteMapping("/{artworkId}/like")
    public Result<Void> removeLike(
            @Parameter(description = "作品ID", required = true) @PathVariable Long artworkId,
            @Parameter(hidden = true) Authentication authentication) {
        if (artworkId == null || artworkId <= 0) {
            return Result.error("参数错误：artworkId非法");
        }
        User user = userService.getCurrentUser();
        if (user == null) {
            return Result.unauthorized("未登录");
        }
        artworkLikeService.removeLike(artworkId, user);
        return Result.success(null);
    }

    /**
     * 获取用户点赞的作品列表
     * 注意：分页参数需为正数，Artwork返回字段需避免懒加载序列化
     * @param authentication 认证信息
     * @param page 页码
     * @param size 每页大小
     * @return 点赞的作品分页列表
     */
    @PreAuthorize("hasRole('USER') or hasRole('ROLE_USER')")
    @Operation(summary = "获取用户点赞的作品列表", description = "获取当前用户点赞的作品分页列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/likes")
    public Result<Page<Artwork>> getLikes(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        if (page <= 0 || size <= 0) {
            return Result.error("参数错误：分页参数需为正数");
        }
        User user = userService.getCurrentUser();
        if (user == null) {
            return Result.unauthorized("未登录");
        }
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "likeTime"));
        Page<Artwork> artworkPage = artworkLikeService.getUserLikes(user.getId(), pageRequest);
        // 建议Artwork实体避免懒加载字段序列化
        return Result.success(artworkPage);
    }

    /**
     * 检查作品是否已点赞
     * @param artworkId 作品ID
     * @param authentication 认证信息
     * @return 是否已点赞
     */
    @PreAuthorize("hasRole('USER') or hasRole('ROLE_USER')")
    @Operation(summary = "检查作品是否已点赞", description = "检查当前用户是否已点赞指定作品")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
            @ApiResponse(responseCode = "404", description = "作品不存在")
    })
    @GetMapping("/{artworkId}/like/check")
    public Result<Boolean> checkLike(
            @Parameter(description = "作品ID", required = true) @PathVariable Long artworkId,
            @Parameter(hidden = true) Authentication authentication) {
        if (artworkId == null || artworkId <= 0) {
            return Result.error("参数错误：artworkId非法");
        }
        User user = userService.getCurrentUser();
        if (user == null) {
            return Result.unauthorized("未登录");
        }
        return Result.success(artworkLikeService.isLiked(artworkId, user.getId()));
    }
}
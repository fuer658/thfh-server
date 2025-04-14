package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.model.Artwork;
import com.thfh.model.User;
import com.thfh.service.ArtworkFavoriteService;
import com.thfh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/{artworkId}/favorite")
    public Result<Void> addFavorite(
            @PathVariable Long artworkId,
            Authentication authentication) {
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
    @DeleteMapping("/{artworkId}/favorite")
    public Result<Void> removeFavorite(
            @PathVariable Long artworkId,
            Authentication authentication) {
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
    @GetMapping("/favorites")
    public Result<Page<Artwork>> getFavorites(
            Authentication authentication,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
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
    @GetMapping("/{artworkId}/favorite/check")
    public Result<Boolean> checkFavorite(
            @PathVariable Long artworkId,
            Authentication authentication) {
        User user = userService.getCurrentUser();
        return Result.success(artworkFavoriteService.isFavorited(artworkId, user.getId()));
    }
}
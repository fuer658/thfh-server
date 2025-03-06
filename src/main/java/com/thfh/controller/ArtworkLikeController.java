package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.model.Artwork;
import com.thfh.model.User;
import com.thfh.service.ArtworkLikeService;
import com.thfh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/{artworkId}/like")
    public Result<Void> addLike(
            @PathVariable Long artworkId,
            Authentication authentication) {
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
    @DeleteMapping("/{artworkId}/like")
    public Result<Void> removeLike(
            @PathVariable Long artworkId,
            Authentication authentication) {
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
    @GetMapping("/likes")
    public Result<Page<Artwork>> getLikes(
            Authentication authentication,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = userService.getCurrentUser();
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(artworkLikeService.getUserLikes(user.getId(), pageRequest));
    }

    /**
     * 检查作品是否已点赞
     * @param artworkId 作品ID
     * @param authentication 认证信息
     * @return 是否已点赞
     */
    @GetMapping("/{artworkId}/like/check")
    public Result<Boolean> checkLike(
            @PathVariable Long artworkId,
            Authentication authentication) {
        User user = userService.getCurrentUser();
        return Result.success(artworkLikeService.isLiked(artworkId, user.getId()));
    }
}
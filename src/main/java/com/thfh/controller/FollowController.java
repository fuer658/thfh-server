package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.FollowDTO;
import com.thfh.model.Follow;
import com.thfh.service.FollowService;
import com.thfh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 关注关系控制器
 * 提供用户关注、取消关注、查询关注列表和粉丝列表等功能
 */
@RestController
@RequestMapping("/api")
public class FollowController {
    @Autowired
    private FollowService followService;
    
    @Autowired
    private UserService userService;

    /**
     * 关注用户
     * @param authentication 认证信息
     * @param followedId 被关注用户ID
     * @return 操作结果
     */
    @PostMapping("/follow/{followedId}")
    public Result<?> follow(Authentication authentication, @PathVariable Long followedId) {
        try {
            followService.follow(followedId);
            return Result.success("关注成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消关注用户
     * @param authentication 认证信息
     * @param followedId 被取消关注的用户ID
     * @return 操作结果
     */
    @DeleteMapping("/unfollow/{followedId}")
    public Result<?> unfollow(Authentication authentication, @PathVariable Long followedId) {
        followService.unfollow(followedId);
        return Result.success("取消关注成功");
    }

    /**
     * 获取当前用户的关注列表
     * @param authentication 认证信息
     * @return 关注用户列表
     */
    @GetMapping("/following")
    public Result<List<FollowDTO>> getFollowingList(Authentication authentication) {
        Long userId = userService.getUserInfo(authentication.getName()).getId();
        return Result.success(followService.getFollowingList(userId));
    }

    /**
     * 获取当前用户的粉丝列表
     * @param authentication 认证信息
     * @return 粉丝用户列表
     */
    @GetMapping("/followers")
    public Result<List<FollowDTO>> getFollowersList(Authentication authentication) {
        Long userId = userService.getUserInfo(authentication.getName()).getId();
        return Result.success(followService.getFollowersList(userId));
    }

    /**
     * 获取当前用户的关注数量
     * @param authentication 认证信息
     * @return 关注用户数量
     */
    @GetMapping("/following/count")
    public Result<Long> getFollowingCount(Authentication authentication) {
        Long userId = userService.getUserInfo(authentication.getName()).getId();
        return Result.success(followService.getFollowingCount(userId));
    }

    /**
     * 获取当前用户的粉丝数量
     * @param authentication 认证信息
     * @return 粉丝用户数量
     */
    @GetMapping("/followers/count")
    public Result<Long> getFollowersCount(Authentication authentication) {
        Long userId = userService.getUserInfo(authentication.getName()).getId();
        return Result.success(followService.getFollowersCount(userId));
    }

    /**
     * 检查当前用户是否关注了指定用户
     * @param authentication 认证信息
     * @param followedId 被检查的用户ID
     * @return 是否已关注
     */
    @GetMapping("/check/{followedId}")
    public ResponseEntity<Boolean> isFollowing(Authentication authentication, @PathVariable Long followedId) {
        Long followerId = userService.getUserInfo(authentication.getName()).getId();
        return ResponseEntity.ok(followService.isFollowing(followerId, followedId));
    }
}
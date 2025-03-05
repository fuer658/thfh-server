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

@RestController
@RequestMapping("/api")
public class FollowController {
    @Autowired
    private FollowService followService;
    
    @Autowired
    private UserService userService;

    @PostMapping("/follow/{followedId}")
    public Result<?> follow(Authentication authentication, @PathVariable Long followedId) {
        try {
            followService.follow(followedId);
            return Result.success("关注成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/unfollow/{followedId}")
    public Result<?> unfollow(Authentication authentication, @PathVariable Long followedId) {
        followService.unfollow(followedId);
        return Result.success("取消关注成功");
    }

    @GetMapping("/following")
    public Result<List<FollowDTO>> getFollowingList(Authentication authentication) {
        Long userId = userService.getUserInfo(authentication.getName()).getId();
        return Result.success(followService.getFollowingList(userId));
    }

    @GetMapping("/followers")
    public Result<List<FollowDTO>> getFollowersList(Authentication authentication) {
        Long userId = userService.getUserInfo(authentication.getName()).getId();
        return Result.success(followService.getFollowersList(userId));
    }

    @GetMapping("/following/count")
    public Result<Long> getFollowingCount(Authentication authentication) {
        Long userId = userService.getUserInfo(authentication.getName()).getId();
        return Result.success(followService.getFollowingCount(userId));
    }

    @GetMapping("/followers/count")
    public Result<Long> getFollowersCount(Authentication authentication) {
        Long userId = userService.getUserInfo(authentication.getName()).getId();
        return Result.success(followService.getFollowersCount(userId));
    }

    @GetMapping("/check/{followedId}")
    public ResponseEntity<Boolean> isFollowing(Authentication authentication, @PathVariable Long followedId) {
        Long followerId = userService.getUserInfo(authentication.getName()).getId();
        return ResponseEntity.ok(followService.isFollowing(followerId, followedId));
    }
}
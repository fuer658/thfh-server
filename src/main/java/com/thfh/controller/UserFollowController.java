package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.model.User;
import com.thfh.service.UserFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-follow")
public class UserFollowController {
    @Autowired
    private UserFollowService userFollowService;

    @PostMapping("/{followingId}")
    public Result<Void> follow(@PathVariable Long followingId, @RequestParam Long followerId) {
        userFollowService.follow(followerId, followingId);
        return Result.success(null);
    }

    @DeleteMapping("/{followingId}")
    public Result<Void> unfollow(@PathVariable Long followingId, @RequestParam Long followerId) {
        userFollowService.unfollow(followerId, followingId);
        return Result.success(null);
    }

    @GetMapping("/check")
    public Result<Boolean> isFollowing(@RequestParam Long followerId, @RequestParam Long followingId) {
        return Result.success(userFollowService.isFollowing(followerId, followingId));
    }

    @GetMapping("/following/{userId}")
    public Result<Page<User>> getFollowingList(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(userFollowService.getFollowingList(userId, PageRequest.of(page, size)));
    }

    @GetMapping("/follower/{userId}")
    public Result<Page<User>> getFollowerList(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(userFollowService.getFollowerList(userId, PageRequest.of(page, size)));
    }

    @GetMapping("/count/{userId}")
    public Result<Long[]> getFollowCounts(@PathVariable Long userId) {
        Long[] counts = new Long[2];
        counts[0] = userFollowService.getFollowingCount(userId);
        counts[1] = userFollowService.getFollowerCount(userId);
        return Result.success(counts);
    }
} 
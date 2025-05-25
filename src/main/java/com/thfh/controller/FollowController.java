package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.FollowDTO;
import com.thfh.service.FollowService;
import com.thfh.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * 关注关系控制器
 * 提供用户关注、取消关注、查询关注列表和粉丝列表等功能
 */
@Tag(name = "用户关注管理", description = "提供用户关注、取消关注、查询关注列表和粉丝列表等功能")
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
    @Operation(summary = "关注用户", description = "关注指定ID的用户")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "关注成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误或已关注该用户"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "被关注用户不存在")
    })
    @PostMapping("/follow/{followedId}")
    public Result<?> follow(
            @Parameter(description = "认证信息", hidden = true) Authentication authentication, 
            @Parameter(description = "被关注用户ID", required = true) @PathVariable Long followedId) {
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
    @Operation(summary = "取消关注", description = "取消关注指定ID的用户")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "取消关注成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "未关注该用户或用户不存在")
    })
    @DeleteMapping("/unfollow/{followedId}")
    public Result<?> unfollow(
            @Parameter(description = "认证信息", hidden = true) Authentication authentication, 
            @Parameter(description = "被取消关注的用户ID", required = true) @PathVariable Long followedId) {
        followService.unfollow(followedId);
        return Result.success("取消关注成功");
    }

    /**
     * 获取当前用户的关注列表
     * @param authentication 认证信息
     * @return 关注用户列表
     */
    @Operation(summary = "获取关注列表", description = "获取当前登录用户的关注列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/following")
    public Result<List<FollowDTO>> getFollowingList(
            @Parameter(description = "认证信息", hidden = true) Authentication authentication) {
        Long userId = userService.getUserInfo(authentication.getName()).getId();
        return Result.success(followService.getFollowingList(userId));
    }

    /**
     * 获取当前用户的粉丝列表
     * @param authentication 认证信息
     * @return 粉丝用户列表
     */
    @Operation(summary = "获取粉丝列表", description = "获取当前登录用户的粉丝列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/followers")
    public Result<List<FollowDTO>> getFollowersList(
            @Parameter(description = "认证信息", hidden = true) Authentication authentication) {
        Long userId = userService.getUserInfo(authentication.getName()).getId();
        return Result.success(followService.getFollowersList(userId));
    }

    /**
     * 获取当前用户的关注数量
     * @param authentication 认证信息
     * @return 关注用户数量
     */
    @Operation(summary = "获取关注数量", description = "获取当前登录用户的关注数量")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/following/count")
    public Result<Long> getFollowingCount(
            @Parameter(description = "认证信息", hidden = true) Authentication authentication) {
        Long userId = userService.getUserInfo(authentication.getName()).getId();
        return Result.success(followService.getFollowingCount(userId));
    }

    /**
     * 获取当前用户的粉丝数量
     * @param authentication 认证信息
     * @return 粉丝用户数量
     */
    @Operation(summary = "获取粉丝数量", description = "获取当前登录用户的粉丝数量")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/followers/count")
    public Result<Long> getFollowersCount(
            @Parameter(description = "认证信息", hidden = true) Authentication authentication) {
        Long userId = userService.getUserInfo(authentication.getName()).getId();
        return Result.success(followService.getFollowersCount(userId));
    }

    /**
     * 检查当前用户是否关注了指定用户
     * @param authentication 认证信息
     * @param followedId 被检查的用户ID
     * @return 是否已关注
     */
    @Operation(summary = "检查是否已关注", description = "检查当前登录用户是否已关注指定ID的用户")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "检查成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限执行此操作")
    })
    @GetMapping("/check/{followedId}")
    @PreAuthorize("hasRole('USER')")
    public Result<Boolean> isFollowing(
            @Parameter(description = "认证信息", hidden = true) Authentication authentication, 
            @Parameter(description = "被检查的用户ID", required = true) @PathVariable Long followedId) {
        Long followerId = userService.getUserInfo(authentication.getName()).getId();
        return Result.success(followService.isFollowing(followerId, followedId));
    }
}
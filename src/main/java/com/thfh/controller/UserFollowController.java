package com.thfh.controller;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import com.thfh.common.Result;
import com.thfh.model.User;
import com.thfh.service.UserFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户关注管理", description = "用户关注相关的API接口")
@RestController
@RequestMapping("/api/user-follow")
public class UserFollowController {
    @Autowired
    private UserFollowService userFollowService;

    /**
     * 关注用户
     * @param followingId 被关注用户ID
     * @param followerId 关注用户ID
     * @return 操作结果
     */
    @Operation(summary = "关注用户", description = "用户关注另一个用户")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "关注成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @PostMapping("/{followingId}")
    public Result<Void> follow(
            @Parameter(description = "被关注用户ID", required = true) @PathVariable Long followingId,
            @Parameter(description = "关注用户ID", required = true) @RequestParam Long followerId) {
        userFollowService.follow(followerId, followingId);
        return Result.success(null);
    }

    /**
     * 取消关注用户
     * @param followingId 被取消关注用户ID
     * @param followerId 取消关注用户ID
     * @return 操作结果
     */
    @Operation(summary = "取消关注用户", description = "用户取消关注另一个用户")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "取消关注成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @DeleteMapping("/{followingId}")
    public Result<Void> unfollow(
            @Parameter(description = "被取消关注用户ID", required = true) @PathVariable Long followingId,
            @Parameter(description = "取消关注用户ID", required = true) @RequestParam Long followerId) {
        userFollowService.unfollow(followerId, followingId);
        return Result.success(null);
    }

    /**
     * 检查是否关注
     * @param followerId 关注用户ID
     * @param followingId 被关注用户ID
     * @return 是否关注
     */
    @Operation(summary = "检查是否关注", description = "检查一个用户是否关注了另一个用户")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/check")
    public Result<Boolean> isFollowing(
            @Parameter(description = "关注用户ID", required = true) @RequestParam Long followerId,
            @Parameter(description = "被关注用户ID", required = true) @RequestParam Long followingId) {
        return Result.success(userFollowService.isFollowing(followerId, followingId));
    }

    /**
     * 获取关注列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 关注用户分页列表
     */
    @Operation(summary = "获取关注列表", description = "获取指定用户关注的用户列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/following/{userId}")
    public Result<Page<User>> getFollowingList(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        return Result.success(userFollowService.getFollowingList(userId, PageRequest.of(page, size)));
    }

    /**
     * 获取粉丝列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 粉丝用户分页列表
     */
    @Operation(summary = "获取粉丝列表", description = "获取指定用户的粉丝列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/follower/{userId}")
    public Result<Page<User>> getFollowerList(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        return Result.success(userFollowService.getFollowerList(userId, PageRequest.of(page, size)));
    }

    /**
     * 获取关注和粉丝数量
     * @param userId 用户ID
     * @return 包含关注数和粉丝数的数组
     */
    @Operation(summary = "获取关注和粉丝数量", description = "获取指定用户的关注数和粉丝数")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/count/{userId}")
    public Result<Long[]> getFollowCounts(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        Long[] counts = new Long[2];
        counts[0] = userFollowService.getFollowingCount(userId);
        counts[1] = userFollowService.getFollowerCount(userId);
        return Result.success(counts);
    }
}
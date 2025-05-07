package com.thfh.controller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import com.thfh.common.Result;
import com.thfh.model.User;
import com.thfh.service.UserFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户关注管理", description = "用户关注相关的API接口")
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
    @ApiOperation(value = "关注用户", notes = "用户关注另一个用户")
    @ApiResponses({
        @ApiResponse(code = 200, message = "关注成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @PostMapping("/{followingId}")
    public Result<Void> follow(
            @ApiParam(value = "被关注用户ID", required = true) @PathVariable Long followingId,
            @ApiParam(value = "关注用户ID", required = true) @RequestParam Long followerId) {
        userFollowService.follow(followerId, followingId);
        return Result.success(null);
    }

    /**
     * 取消关注用户
     * @param followingId 被取消关注用户ID
     * @param followerId 取消关注用户ID
     * @return 操作结果
     */
    @ApiOperation(value = "取消关注用户", notes = "用户取消关注另一个用户")
    @ApiResponses({
        @ApiResponse(code = 200, message = "取消关注成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @DeleteMapping("/{followingId}")
    public Result<Void> unfollow(
            @ApiParam(value = "被取消关注用户ID", required = true) @PathVariable Long followingId,
            @ApiParam(value = "取消关注用户ID", required = true) @RequestParam Long followerId) {
        userFollowService.unfollow(followerId, followingId);
        return Result.success(null);
    }

    /**
     * 检查是否关注
     * @param followerId 关注用户ID
     * @param followingId 被关注用户ID
     * @return 是否关注
     */
    @ApiOperation(value = "检查是否关注", notes = "检查一个用户是否关注了另一个用户")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/check")
    public Result<Boolean> isFollowing(
            @ApiParam(value = "关注用户ID", required = true) @RequestParam Long followerId,
            @ApiParam(value = "被关注用户ID", required = true) @RequestParam Long followingId) {
        return Result.success(userFollowService.isFollowing(followerId, followingId));
    }

    /**
     * 获取关注列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 关注用户分页列表
     */
    @ApiOperation(value = "获取关注列表", notes = "获取指定用户关注的用户列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/following/{userId}")
    public Result<Page<User>> getFollowingList(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "页码", defaultValue = "0") @RequestParam(defaultValue = "0") int page,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        return Result.success(userFollowService.getFollowingList(userId, PageRequest.of(page, size)));
    }

    /**
     * 获取粉丝列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 粉丝用户分页列表
     */
    @ApiOperation(value = "获取粉丝列表", notes = "获取指定用户的粉丝列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/follower/{userId}")
    public Result<Page<User>> getFollowerList(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "页码", defaultValue = "0") @RequestParam(defaultValue = "0") int page,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        return Result.success(userFollowService.getFollowerList(userId, PageRequest.of(page, size)));
    }

    /**
     * 获取关注和粉丝数量
     * @param userId 用户ID
     * @return 包含关注数和粉丝数的数组
     */
    @ApiOperation(value = "获取关注和粉丝数量", notes = "获取指定用户的关注数和粉丝数")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/count/{userId}")
    public Result<Long[]> getFollowCounts(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        Long[] counts = new Long[2];
        counts[0] = userFollowService.getFollowingCount(userId);
        counts[1] = userFollowService.getFollowerCount(userId);
        return Result.success(counts);
    }
}
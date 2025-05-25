package com.thfh.controller;

import com.thfh.dto.FriendRequestDTO;
import com.thfh.dto.FriendDTO;
import com.thfh.service.FriendService;
import com.thfh.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "好友管理")
@RestController
@RequestMapping("/api/friend")
public class FriendController {
    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    /**
     * 发送好友请求
     */
    @Operation(summary = "发送好友请求")
    @PostMapping("/request")
    public Result<String> sendFriendRequest(
            @Parameter(description = "发起人用户ID", required = true) @RequestParam Long fromUserId,
            @Parameter(description = "接收人用户ID", required = true) @RequestParam Long toUserId) {
        return Result.success(friendService.sendFriendRequest(fromUserId, toUserId));
    }

    /**
     * 处理好友请求
     */
    @Operation(summary = "处理好友请求（同意/拒绝）")
    @PostMapping("/request/handle")
    public Result<String> handleFriendRequest(
            @Parameter(description = "请求ID", required = true) @RequestParam Long requestId,
            @Parameter(description = "是否同意", required = true, example = "true") @RequestParam boolean accept) {
        return Result.success(friendService.handleFriendRequest(requestId, accept));
    }

    /**
     * 查询好友列表
     * @apiNote 返回内容包含好友用户名friendName
     */
    @Operation(summary = "查询好友列表", description = "返回内容包含好友用户名friendName")
    @GetMapping("/list")
    public Result<List<FriendDTO>> listFriends(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        return Result.success(friendService.listFriends(userId));
    }

    /**
     * 删除好友
     */
    @Operation(summary = "删除好友")
    @PostMapping("/delete")
    public Result<String> deleteFriend(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "好友ID", required = true) @RequestParam Long friendId) {
        return Result.success(friendService.deleteFriend(userId, friendId));
    }

    /**
     * 查询我收到的好友请求（支持筛选待处理/全部）
     */
    @Operation(summary = "查询我收到的好友请求（支持筛选待处理/全部）")
    @GetMapping("/request/received")
    public Result<List<FriendRequestDTO>> getReceivedFriendRequests(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "请求状态(pending/accepted/rejected)", required = false) @RequestParam(required = false) String status) {
        return Result.success(friendService.getReceivedFriendRequests(userId, status));
    }

    /**
     * 查询我发出的好友请求（支持筛选待处理/全部）
     */
    @Operation(summary = "查询我发出的好友请求（支持筛选待处理/全部）")
    @GetMapping("/request/sent")
    public Result<List<FriendRequestDTO>> getSentFriendRequests(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "请求状态(pending/accepted/rejected)", required = false) @RequestParam(required = false) String status) {
        return Result.success(friendService.getSentFriendRequests(userId, status));
    }

    /**
     * 拉黑/屏蔽好友
     */
    @Operation(summary = "拉黑/屏蔽好友")
    @PostMapping("/block")
    public Result<String> blockFriend(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "目标用户ID", required = true) @RequestParam Long targetUserId) {
        return Result.success(friendService.blockFriend(userId, targetUserId));
    }

    /**
     * 取消拉黑/解除屏蔽
     */
    @Operation(summary = "取消拉黑/解除屏蔽")
    @PostMapping("/unblock")
    public Result<String> unblockFriend(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "目标用户ID", required = true) @RequestParam Long targetUserId) {
        return Result.success(friendService.unblockFriend(userId, targetUserId));
    }

    /**
     * 设置好友备注
     */
    @Operation(summary = "设置好友备注")
    @PostMapping("/remark")
    public Result<String> setFriendRemark(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "好友ID", required = true) @RequestParam Long friendId,
            @Parameter(description = "备注名", required = true) @RequestParam String remark) {
        return Result.success(friendService.setFriendRemark(userId, friendId, remark));
    }

    /**
     * 查询好友详情
     * @apiNote 返回内容包含好友用户名friendName
     */
    @Operation(summary = "查询好友详情", description = "返回内容包含好友用户名friendName")
    @GetMapping("/detail")
    public Result<FriendDTO> getFriendDetail(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "好友ID", required = true) @RequestParam Long friendId) {
        return Result.success(friendService.getFriendDetail(userId, friendId));
    }

    /**
     * 撤回好友请求
     */
    @Operation(summary = "撤回好友请求")
    @PostMapping("/request/cancel")
    public Result<String> cancelFriendRequest(
            @Parameter(description = "请求ID", required = true) @RequestParam Long requestId,
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        return Result.success(friendService.cancelFriendRequest(requestId, userId));
    }

    /**
     * 判断两用户是否为好友
     */
    @Operation(summary = "判断两用户是否为好友")
    @GetMapping("/check")
    public Result<Boolean> isFriend(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "好友ID", required = true) @RequestParam Long friendId) {
        return Result.success(friendService.isFriend(userId, friendId));
    }
} 
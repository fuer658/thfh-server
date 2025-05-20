package com.thfh.controller;

import com.thfh.dto.FriendRequestDTO;
import com.thfh.dto.FriendDTO;
import com.thfh.service.FriendService;
import com.thfh.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "好友管理")
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
    @ApiOperation("发送好友请求")
    @PostMapping("/request")
    public Result<String> sendFriendRequest(
            @ApiParam(value = "发起人用户ID", required = true) @RequestParam Long fromUserId,
            @ApiParam(value = "接收人用户ID", required = true) @RequestParam Long toUserId) {
        return Result.success(friendService.sendFriendRequest(fromUserId, toUserId));
    }

    /**
     * 处理好友请求
     */
    @ApiOperation("处理好友请求（同意/拒绝）")
    @PostMapping("/request/handle")
    public Result<String> handleFriendRequest(
            @ApiParam(value = "请求ID", required = true) @RequestParam Long requestId,
            @ApiParam(value = "是否同意", required = true, example = "true") @RequestParam boolean accept) {
        return Result.success(friendService.handleFriendRequest(requestId, accept));
    }

    /**
     * 查询好友列表
     * @apiNote 返回内容包含好友用户名friendName
     */
    @ApiOperation(value = "查询好友列表", notes = "返回内容包含好友用户名friendName")
    @GetMapping("/list")
    public Result<List<FriendDTO>> listFriends(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId) {
        return Result.success(friendService.listFriends(userId));
    }

    /**
     * 删除好友
     */
    @ApiOperation("删除好友")
    @PostMapping("/delete")
    public Result<String> deleteFriend(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "好友ID", required = true) @RequestParam Long friendId) {
        return Result.success(friendService.deleteFriend(userId, friendId));
    }

    /**
     * 查询我收到的好友请求（支持筛选待处理/全部）
     */
    @ApiOperation("查询我收到的好友请求（支持筛选待处理/全部）")
    @GetMapping("/request/received")
    public Result<List<FriendRequestDTO>> getReceivedFriendRequests(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "请求状态(pending/accepted/rejected)", required = false) @RequestParam(required = false) String status) {
        return Result.success(friendService.getReceivedFriendRequests(userId, status));
    }

    /**
     * 查询我发出的好友请求（支持筛选待处理/全部）
     */
    @ApiOperation("查询我发出的好友请求（支持筛选待处理/全部）")
    @GetMapping("/request/sent")
    public Result<List<FriendRequestDTO>> getSentFriendRequests(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "请求状态(pending/accepted/rejected)", required = false) @RequestParam(required = false) String status) {
        return Result.success(friendService.getSentFriendRequests(userId, status));
    }

    /**
     * 拉黑/屏蔽好友
     */
    @ApiOperation("拉黑/屏蔽好友")
    @PostMapping("/block")
    public Result<String> blockFriend(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "目标用户ID", required = true) @RequestParam Long targetUserId) {
        return Result.success(friendService.blockFriend(userId, targetUserId));
    }

    /**
     * 取消拉黑/解除屏蔽
     */
    @ApiOperation("取消拉黑/解除屏蔽")
    @PostMapping("/unblock")
    public Result<String> unblockFriend(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "目标用户ID", required = true) @RequestParam Long targetUserId) {
        return Result.success(friendService.unblockFriend(userId, targetUserId));
    }

    /**
     * 设置好友备注
     */
    @ApiOperation("设置好友备注")
    @PostMapping("/remark")
    public Result<String> setFriendRemark(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "好友ID", required = true) @RequestParam Long friendId,
            @ApiParam(value = "备注名", required = true) @RequestParam String remark) {
        return Result.success(friendService.setFriendRemark(userId, friendId, remark));
    }

    /**
     * 查询好友详情
     * @apiNote 返回内容包含好友用户名friendName
     */
    @ApiOperation(value = "查询好友详情", notes = "返回内容包含好友用户名friendName")
    @GetMapping("/detail")
    public Result<FriendDTO> getFriendDetail(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "好友ID", required = true) @RequestParam Long friendId) {
        return Result.success(friendService.getFriendDetail(userId, friendId));
    }

    /**
     * 撤回好友请求
     */
    @ApiOperation("撤回好友请求")
    @PostMapping("/request/cancel")
    public Result<String> cancelFriendRequest(
            @ApiParam(value = "请求ID", required = true) @RequestParam Long requestId,
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId) {
        return Result.success(friendService.cancelFriendRequest(requestId, userId));
    }

    /**
     * 判断两用户是否为好友
     */
    @ApiOperation("判断两用户是否为好友")
    @GetMapping("/check")
    public Result<Boolean> isFriend(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "好友ID", required = true) @RequestParam Long friendId) {
        return Result.success(friendService.isFriend(userId, friendId));
    }
} 
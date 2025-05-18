package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.PostDTO;
import com.thfh.model.PostBrowseHistory;
import com.thfh.service.PostBrowseHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "动态浏览记录", description = "提供用户浏览动态的历史记录管理功能")
@RestController
@RequestMapping("/api/posts/browse-history")
public class PostBrowseHistoryController {

    @Autowired
    private PostBrowseHistoryService postBrowseHistoryService;

    /**
     * 记录用户浏览动态的历史
     */
    @ApiOperation(value = "记录浏览历史", notes = "记录用户浏览动态的历史，同时增加动态浏览量")
    @ApiResponses({
        @ApiResponse(code = 200, message = "记录成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "动态不存在")
    })
    @PostMapping("/{postId}")
    @PreAuthorize("hasRole('USER')")
    public Result<PostBrowseHistory> recordBrowseHistory(
            @ApiParam(value = "动态ID", required = true) @PathVariable Long postId) {
        return Result.success(postBrowseHistoryService.recordBrowseHistory(postId));
    }

    /**
     * 获取用户动态浏览历史
     */
    @ApiOperation(value = "获取浏览历史", notes = "分页获取当前用户的动态浏览历史")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Result<Page<PostDTO>> getUserBrowseHistory(
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(postBrowseHistoryService.getUserBrowseHistory(pageRequest));
    }

    /**
     * 获取最近浏览的动态
     */
    @ApiOperation(value = "获取最近浏览记录", notes = "获取当前用户最近浏览过的动态列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/recent")
    @PreAuthorize("hasRole('USER')")
    public Result<List<PostDTO>> getRecentBrowsedPosts(
            @ApiParam(value = "记录数量限制", defaultValue = "5") @RequestParam(defaultValue = "5") int limit) {
        return Result.success(postBrowseHistoryService.getRecentBrowsedPosts(limit));
    }

    /**
     * 删除浏览记录
     */
    @ApiOperation(value = "删除浏览记录", notes = "删除指定的浏览记录")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "无权删除该记录"),
        @ApiResponse(code = 404, message = "记录不存在")
    })
    @DeleteMapping("/{historyId}")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> deleteHistory(
            @ApiParam(value = "记录ID", required = true) @PathVariable Long historyId) {
        postBrowseHistoryService.deleteHistory(historyId);
        return Result.success(null);
    }

    /**
     * 清空所有浏览记录
     */
    @ApiOperation(value = "清空浏览记录", notes = "清空当前用户的所有浏览记录")
    @ApiResponses({
        @ApiResponse(code = 200, message = "清空成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public Result<Void> clearUserHistory() {
        postBrowseHistoryService.clearUserHistory();
        return Result.success(null);
    }

    /**
     * 管理员查看用户浏览历史
     */
    @ApiOperation(value = "管理员查看用户浏览历史", notes = "管理员查看指定用户的动态浏览历史")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "无权访问，需要管理员权限"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @GetMapping("/admin/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<PostDTO>> getUserBrowseHistoryByAdmin(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(postBrowseHistoryService.getUserBrowseHistoryByAdmin(userId, pageRequest));
    }

    /**
     * 管理员删除浏览记录
     */
    @ApiOperation(value = "管理员删除浏览记录", notes = "管理员删除任意浏览记录")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "无权访问，需要管理员权限"),
        @ApiResponse(code = 404, message = "记录不存在")
    })
    @DeleteMapping("/admin/{historyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteHistoryByAdmin(
            @ApiParam(value = "记录ID", required = true) @PathVariable Long historyId) {
        postBrowseHistoryService.deleteHistoryByAdmin(historyId);
        return Result.success(null);
    }

    /**
     * 管理员清空用户浏览记录
     */
    @ApiOperation(value = "管理员清空用户浏览记录", notes = "管理员清空指定用户的所有浏览记录")
    @ApiResponses({
        @ApiResponse(code = 200, message = "清空成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "无权访问，需要管理员权限"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @DeleteMapping("/admin/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> clearUserHistoryByAdmin(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        postBrowseHistoryService.clearUserHistoryByAdmin(userId);
        return Result.success(null);
    }
} 
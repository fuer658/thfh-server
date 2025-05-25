package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.PostDTO;
import com.thfh.model.PostBrowseHistory;
import com.thfh.service.PostBrowseHistoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "动态浏览记录", description = "提供用户浏览动态的历史记录管理功能")
@RestController
@RequestMapping("/api/posts/browse-history")
public class PostBrowseHistoryController {

    @Autowired
    private PostBrowseHistoryService postBrowseHistoryService;

    /**
     * 记录用户浏览动态的历史
     */
    @Operation(summary = "记录浏览历史", description = "记录用户浏览动态的历史，同时增加动态浏览量")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "记录成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "动态不存在")
    })
    @PostMapping("/{postId}")
    @PreAuthorize("hasRole('USER')")
    public Result<PostBrowseHistory> recordBrowseHistory(
            @Parameter(description = "动态ID", required = true) @PathVariable Long postId) {
        return Result.success(postBrowseHistoryService.recordBrowseHistory(postId));
    }

    /**
     * 获取用户动态浏览历史
     */
    @Operation(summary = "获取浏览历史", description = "分页获取当前用户的动态浏览历史")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Result<Page<PostDTO>> getUserBrowseHistory(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(postBrowseHistoryService.getUserBrowseHistory(pageRequest));
    }

    /**
     * 获取最近浏览的动态
     */
    @Operation(summary = "获取最近浏览记录", description = "获取当前用户最近浏览过的动态列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/recent")
    @PreAuthorize("hasRole('USER')")
    public Result<List<PostDTO>> getRecentBrowsedPosts(
            @Parameter(description = "记录数量限制") @RequestParam(defaultValue = "5") int limit) {
        return Result.success(postBrowseHistoryService.getRecentBrowsedPosts(limit));
    }

    /**
     * 删除浏览记录
     */
    @Operation(summary = "删除浏览记录", description = "删除指定的浏览记录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "无权删除该记录"),
        @ApiResponse(responseCode = "404", description = "记录不存在")
    })
    @DeleteMapping("/{historyId}")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> deleteHistory(
            @Parameter(description = "记录ID", required = true) @PathVariable Long historyId) {
        postBrowseHistoryService.deleteHistory(historyId);
        return Result.success(null);
    }

    /**
     * 清空所有浏览记录
     */
    @Operation(summary = "清空浏览记录", description = "清空当前用户的所有浏览记录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "清空成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
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
    @Operation(summary = "管理员查看用户浏览历史", description = "管理员查看指定用户的动态浏览历史")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "无权访问，需要管理员权限"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/admin/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<PostDTO>> getUserBrowseHistoryByAdmin(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(postBrowseHistoryService.getUserBrowseHistoryByAdmin(userId, pageRequest));
    }

    /**
     * 管理员删除浏览记录
     */
    @Operation(summary = "管理员删除浏览记录", description = "管理员删除任意浏览记录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "无权访问，需要管理员权限"),
        @ApiResponse(responseCode = "404", description = "记录不存在")
    })
    @DeleteMapping("/admin/{historyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteHistoryByAdmin(
            @Parameter(description = "记录ID", required = true) @PathVariable Long historyId) {
        postBrowseHistoryService.deleteHistoryByAdmin(historyId);
        return Result.success(null);
    }

    /**
     * 管理员清空用户浏览记录
     */
    @Operation(summary = "管理员清空用户浏览记录", description = "管理员清空指定用户的所有浏览记录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "清空成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "无权访问，需要管理员权限"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @DeleteMapping("/admin/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> clearUserHistoryByAdmin(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        postBrowseHistoryService.clearUserHistoryByAdmin(userId);
        return Result.success(null);
    }
} 
package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.model.Artwork;
import com.thfh.model.ArtworkBrowseHistory;
import com.thfh.model.ArtworkType;
import com.thfh.service.ArtworkBrowseHistoryService;
import com.thfh.dto.ArtworkBrowseHistoryDTO;
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

@Tag(name = "作品浏览记录", description = "提供用户浏览作品的历史记录管理功能")
@RestController
@RequestMapping("/api/artworks/browse-history")
public class ArtworkBrowseHistoryController {

    @Autowired
    private ArtworkBrowseHistoryService artworkBrowseHistoryService;

    /**
     * 记录用户浏览作品的历史
     */
    @Operation(summary = "记录浏览历史", description = "记录用户浏览作品的历史，同时增加作品浏览量")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "记录成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "作品不存在")
    })
    @PostMapping("/{artworkId}")
    @PreAuthorize("hasRole('USER')")
    public Result<ArtworkBrowseHistory> recordBrowseHistory(
            @Parameter(description = "作品ID", required = true) @PathVariable Long artworkId) {
        return Result.success(artworkBrowseHistoryService.recordBrowseHistory(artworkId));
    }

    /**
     * 获取用户作品浏览历史
     */
    @Operation(summary = "获取浏览历史", description = "分页获取当前用户的作品浏览历史")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Result<Page<Artwork>> getUserBrowseHistory(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(artworkBrowseHistoryService.getUserBrowseHistory(pageRequest));
    }

    /**
     * 获取最近浏览的作品
     */
    @Operation(summary = "获取最近浏览记录", description = "获取当前用户最近浏览过的作品列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/recent")
    @PreAuthorize("hasRole('USER')")
    public Result<List<Artwork>> getRecentBrowsedArtworks(
            @Parameter(description = "记录数量限制") @RequestParam(defaultValue = "5") int limit) {
        return Result.success(artworkBrowseHistoryService.getRecentBrowsedArtworks(limit));
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
        artworkBrowseHistoryService.deleteHistory(historyId);
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
        artworkBrowseHistoryService.clearUserHistory();
        return Result.success(null);
    }

    /**
     * 管理员查看用户浏览历史
     */
    @Operation(summary = "管理员查看用户浏览历史", description = "管理员分页获取指定用户的作品浏览历史")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<Artwork>> getUserBrowseHistoryByAdmin(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(artworkBrowseHistoryService.getUserBrowseHistoryByAdmin(userId, pageRequest));
    }

    /**
     * 管理员删除浏览记录
     */
    @Operation(summary = "管理员删除浏览记录", description = "管理员删除指定的浏览记录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "记录不存在")
    })
    @DeleteMapping("/admin/{historyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteHistoryByAdmin(
            @Parameter(description = "记录ID", required = true) @PathVariable Long historyId) {
        artworkBrowseHistoryService.deleteHistoryByAdmin(historyId);
        return Result.success(null);
    }

    /**
     * 管理员清空用户的浏览记录
     */
    @Operation(summary = "管理员清空用户浏览记录", description = "管理员清空指定用户的所有浏览记录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "清空成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @DeleteMapping("/admin/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> clearUserHistoryByAdmin(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        artworkBrowseHistoryService.clearUserHistoryByAdmin(userId);
        return Result.success(null);
    }

    /**
     * 获取用户作品浏览历史（按类型过滤）
     */
    @Operation(summary = "按类型获取浏览历史", description = "分页获取当前用户特定类型的作品浏览历史")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('USER')")
    public Result<Page<Artwork>> getUserBrowseHistoryByType(
            @Parameter(description = "作品类型", required = true, example = "PERSONAL") @PathVariable ArtworkType type,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(artworkBrowseHistoryService.getUserBrowseHistoryByType(type, pageRequest));
    }

    /**
     * 获取最近浏览的特定类型作品
     */
    @Operation(summary = "按类型获取最近浏览记录", description = "获取当前用户最近浏览过的特定类型的作品列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/recent/type/{type}")
    @PreAuthorize("hasRole('USER')")
    public Result<List<Artwork>> getRecentBrowsedArtworksByType(
            @Parameter(description = "作品类型", required = true, example = "PERSONAL") @PathVariable ArtworkType type,
            @Parameter(description = "记录数量限制") @RequestParam(defaultValue = "5") int limit) {
        return Result.success(artworkBrowseHistoryService.getRecentBrowsedArtworksByType(type, limit));
    }

    /**
     * 管理员查看用户特定类型作品的浏览历史
     */
    @Operation(summary = "管理员按类型查看用户浏览历史", description = "管理员分页获取指定用户特定类型的作品浏览历史")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/admin/{userId}/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<Artwork>> getUserBrowseHistoryByTypeForAdmin(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "作品类型", required = true, example = "PERSONAL") @PathVariable ArtworkType type,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(artworkBrowseHistoryService.getUserBrowseHistoryByTypeForAdmin(userId, type, pageRequest));
    }

    /**
     * 获取用户作品浏览历史（精简版）
     */
    @Operation(summary = "获取浏览历史（精简版）", description = "分页获取当前用户的作品浏览历史，返回精简数据")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/simple")
    @PreAuthorize("hasRole('USER')")
    public Result<Page<ArtworkBrowseHistoryDTO>> getUserBrowseHistorySimple(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(artworkBrowseHistoryService.getUserBrowseHistoryDTO(pageRequest));
    }

    /**
     * 获取用户作品浏览历史（按类型过滤，精简版）
     */
    @Operation(summary = "按类型获取浏览历史（精简版）", description = "分页获取当前用户特定类型的作品浏览历史，返回精简数据")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/simple/type/{type}")
    @PreAuthorize("hasRole('USER')")
    public Result<Page<ArtworkBrowseHistoryDTO>> getUserBrowseHistoryByTypeSimple(
            @Parameter(description = "作品类型", required = true, example = "PERSONAL") @PathVariable ArtworkType type,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(artworkBrowseHistoryService.getUserBrowseHistoryByTypeDTO(type, pageRequest));
    }

    /**
     * 获取最近浏览的作品（精简版）
     */
    @Operation(summary = "获取最近浏览记录（精简版）", description = "获取当前用户最近浏览过的作品列表，返回精简数据")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/recent/simple")
    @PreAuthorize("hasRole('USER')")
    public Result<List<ArtworkBrowseHistoryDTO>> getRecentBrowsedArtworksSimple(
            @Parameter(description = "记录数量限制") @RequestParam(defaultValue = "5") int limit) {
        return Result.success(artworkBrowseHistoryService.getRecentBrowsedArtworksDTO(limit));
    }

    /**
     * 获取最近浏览的特定类型作品（精简版）
     */
    @Operation(summary = "按类型获取最近浏览记录（精简版）", description = "获取当前用户最近浏览过的特定类型的作品列表，返回精简数据")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/recent/simple/type/{type}")
    @PreAuthorize("hasRole('USER')")
    public Result<List<ArtworkBrowseHistoryDTO>> getRecentBrowsedArtworksByTypeSimple(
            @Parameter(description = "作品类型", required = true, example = "PERSONAL") @PathVariable ArtworkType type,
            @Parameter(description = "记录数量限制") @RequestParam(defaultValue = "5") int limit) {
        return Result.success(artworkBrowseHistoryService.getRecentBrowsedArtworksByTypeDTO(type, limit));
    }

    /**
     * 管理员查看用户浏览历史（精简版）
     */
    @Operation(summary = "管理员查看用户浏览历史（精简版）", description = "管理员分页获取指定用户的作品浏览历史，返回精简数据")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/admin/{userId}/simple")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<ArtworkBrowseHistoryDTO>> getUserBrowseHistoryByAdminSimple(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(artworkBrowseHistoryService.getUserBrowseHistoryByAdminDTO(userId, pageRequest));
    }

    /**
     * 管理员查看用户特定类型作品的浏览历史（精简版）
     */
    @Operation(summary = "管理员按类型查看用户浏览历史（精简版）", description = "管理员分页获取指定用户特定类型的作品浏览历史，返回精简数据")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/admin/{userId}/simple/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<ArtworkBrowseHistoryDTO>> getUserBrowseHistoryByTypeForAdminSimple(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "作品类型", required = true, example = "PERSONAL") @PathVariable ArtworkType type,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(artworkBrowseHistoryService.getUserBrowseHistoryByTypeForAdminDTO(userId, type, pageRequest));
    }
} 
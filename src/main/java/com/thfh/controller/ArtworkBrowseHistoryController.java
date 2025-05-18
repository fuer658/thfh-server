package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.model.Artwork;
import com.thfh.model.ArtworkBrowseHistory;
import com.thfh.model.ArtworkType;
import com.thfh.service.ArtworkBrowseHistoryService;
import com.thfh.dto.ArtworkBrowseHistoryDTO;
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

@Api(tags = "作品浏览记录", description = "提供用户浏览作品的历史记录管理功能")
@RestController
@RequestMapping("/api/artworks/browse-history")
public class ArtworkBrowseHistoryController {

    @Autowired
    private ArtworkBrowseHistoryService artworkBrowseHistoryService;

    /**
     * 记录用户浏览作品的历史
     */
    @ApiOperation(value = "记录浏览历史", notes = "记录用户浏览作品的历史，同时增加作品浏览量")
    @ApiResponses({
        @ApiResponse(code = 200, message = "记录成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "作品不存在")
    })
    @PostMapping("/{artworkId}")
    @PreAuthorize("hasRole('USER')")
    public Result<ArtworkBrowseHistory> recordBrowseHistory(
            @ApiParam(value = "作品ID", required = true) @PathVariable Long artworkId) {
        return Result.success(artworkBrowseHistoryService.recordBrowseHistory(artworkId));
    }

    /**
     * 获取用户作品浏览历史
     */
    @ApiOperation(value = "获取浏览历史", notes = "分页获取当前用户的作品浏览历史")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Result<Page<Artwork>> getUserBrowseHistory(
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(artworkBrowseHistoryService.getUserBrowseHistory(pageRequest));
    }

    /**
     * 获取最近浏览的作品
     */
    @ApiOperation(value = "获取最近浏览记录", notes = "获取当前用户最近浏览过的作品列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/recent")
    @PreAuthorize("hasRole('USER')")
    public Result<List<Artwork>> getRecentBrowsedArtworks(
            @ApiParam(value = "记录数量限制", defaultValue = "5") @RequestParam(defaultValue = "5") int limit) {
        return Result.success(artworkBrowseHistoryService.getRecentBrowsedArtworks(limit));
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
        artworkBrowseHistoryService.deleteHistory(historyId);
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
        artworkBrowseHistoryService.clearUserHistory();
        return Result.success(null);
    }

    /**
     * 管理员查看用户浏览历史
     */
    @ApiOperation(value = "管理员查看用户浏览历史", notes = "管理员分页获取指定用户的作品浏览历史")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "权限不足"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @GetMapping("/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<Artwork>> getUserBrowseHistoryByAdmin(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(artworkBrowseHistoryService.getUserBrowseHistoryByAdmin(userId, pageRequest));
    }

    /**
     * 管理员删除浏览记录
     */
    @ApiOperation(value = "管理员删除浏览记录", notes = "管理员删除指定的浏览记录")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "权限不足"),
        @ApiResponse(code = 404, message = "记录不存在")
    })
    @DeleteMapping("/admin/{historyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteHistoryByAdmin(
            @ApiParam(value = "记录ID", required = true) @PathVariable Long historyId) {
        artworkBrowseHistoryService.deleteHistoryByAdmin(historyId);
        return Result.success(null);
    }

    /**
     * 管理员清空用户的浏览记录
     */
    @ApiOperation(value = "管理员清空用户浏览记录", notes = "管理员清空指定用户的所有浏览记录")
    @ApiResponses({
        @ApiResponse(code = 200, message = "清空成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "权限不足"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @DeleteMapping("/admin/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> clearUserHistoryByAdmin(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        artworkBrowseHistoryService.clearUserHistoryByAdmin(userId);
        return Result.success(null);
    }

    /**
     * 获取用户作品浏览历史（按类型过滤）
     */
    @ApiOperation(value = "按类型获取浏览历史", notes = "分页获取当前用户特定类型的作品浏览历史")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('USER')")
    public Result<Page<Artwork>> getUserBrowseHistoryByType(
            @ApiParam(value = "作品类型", required = true, example = "PERSONAL") @PathVariable ArtworkType type,
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(artworkBrowseHistoryService.getUserBrowseHistoryByType(type, pageRequest));
    }

    /**
     * 获取最近浏览的特定类型作品
     */
    @ApiOperation(value = "按类型获取最近浏览记录", notes = "获取当前用户最近浏览过的特定类型的作品列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/recent/type/{type}")
    @PreAuthorize("hasRole('USER')")
    public Result<List<Artwork>> getRecentBrowsedArtworksByType(
            @ApiParam(value = "作品类型", required = true, example = "PERSONAL") @PathVariable ArtworkType type,
            @ApiParam(value = "记录数量限制", defaultValue = "5") @RequestParam(defaultValue = "5") int limit) {
        return Result.success(artworkBrowseHistoryService.getRecentBrowsedArtworksByType(type, limit));
    }

    /**
     * 管理员查看用户特定类型作品的浏览历史
     */
    @ApiOperation(value = "管理员按类型查看用户浏览历史", notes = "管理员分页获取指定用户特定类型的作品浏览历史")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "权限不足"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @GetMapping("/admin/{userId}/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<Artwork>> getUserBrowseHistoryByTypeForAdmin(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "作品类型", required = true, example = "PERSONAL") @PathVariable ArtworkType type,
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(artworkBrowseHistoryService.getUserBrowseHistoryByTypeForAdmin(userId, type, pageRequest));
    }

    /**
     * 获取用户作品浏览历史（精简版）
     */
    @ApiOperation(value = "获取浏览历史（精简版）", notes = "分页获取当前用户的作品浏览历史，返回精简数据")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/simple")
    @PreAuthorize("hasRole('USER')")
    public Result<Page<ArtworkBrowseHistoryDTO>> getUserBrowseHistorySimple(
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(artworkBrowseHistoryService.getUserBrowseHistoryDTO(pageRequest));
    }

    /**
     * 获取用户作品浏览历史（按类型过滤，精简版）
     */
    @ApiOperation(value = "按类型获取浏览历史（精简版）", notes = "分页获取当前用户特定类型的作品浏览历史，返回精简数据")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/simple/type/{type}")
    @PreAuthorize("hasRole('USER')")
    public Result<Page<ArtworkBrowseHistoryDTO>> getUserBrowseHistoryByTypeSimple(
            @ApiParam(value = "作品类型", required = true, example = "PERSONAL") @PathVariable ArtworkType type,
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(artworkBrowseHistoryService.getUserBrowseHistoryByTypeDTO(type, pageRequest));
    }

    /**
     * 获取最近浏览的作品（精简版）
     */
    @ApiOperation(value = "获取最近浏览记录（精简版）", notes = "获取当前用户最近浏览过的作品列表，返回精简数据")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/recent/simple")
    @PreAuthorize("hasRole('USER')")
    public Result<List<ArtworkBrowseHistoryDTO>> getRecentBrowsedArtworksSimple(
            @ApiParam(value = "记录数量限制", defaultValue = "5") @RequestParam(defaultValue = "5") int limit) {
        return Result.success(artworkBrowseHistoryService.getRecentBrowsedArtworksDTO(limit));
    }

    /**
     * 获取最近浏览的特定类型作品（精简版）
     */
    @ApiOperation(value = "按类型获取最近浏览记录（精简版）", notes = "获取当前用户最近浏览过的特定类型的作品列表，返回精简数据")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/recent/simple/type/{type}")
    @PreAuthorize("hasRole('USER')")
    public Result<List<ArtworkBrowseHistoryDTO>> getRecentBrowsedArtworksByTypeSimple(
            @ApiParam(value = "作品类型", required = true, example = "PERSONAL") @PathVariable ArtworkType type,
            @ApiParam(value = "记录数量限制", defaultValue = "5") @RequestParam(defaultValue = "5") int limit) {
        return Result.success(artworkBrowseHistoryService.getRecentBrowsedArtworksByTypeDTO(type, limit));
    }

    /**
     * 管理员查看用户浏览历史（精简版）
     */
    @ApiOperation(value = "管理员查看用户浏览历史（精简版）", notes = "管理员分页获取指定用户的作品浏览历史，返回精简数据")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "权限不足"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @GetMapping("/admin/{userId}/simple")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<ArtworkBrowseHistoryDTO>> getUserBrowseHistoryByAdminSimple(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(artworkBrowseHistoryService.getUserBrowseHistoryByAdminDTO(userId, pageRequest));
    }

    /**
     * 管理员查看用户特定类型作品的浏览历史（精简版）
     */
    @ApiOperation(value = "管理员按类型查看用户浏览历史（精简版）", notes = "管理员分页获取指定用户特定类型的作品浏览历史，返回精简数据")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "权限不足"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @GetMapping("/admin/{userId}/simple/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<ArtworkBrowseHistoryDTO>> getUserBrowseHistoryByTypeForAdminSimple(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "作品类型", required = true, example = "PERSONAL") @PathVariable ArtworkType type,
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastBrowseTime"));
        return Result.success(artworkBrowseHistoryService.getUserBrowseHistoryByTypeForAdminDTO(userId, type, pageRequest));
    }
} 
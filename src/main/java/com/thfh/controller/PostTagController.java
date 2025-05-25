package com.thfh.controller;

import com.thfh.model.Post;
import com.thfh.model.PostTag;
import com.thfh.service.PostService;
import com.thfh.service.PostTagService;
import com.thfh.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 动态标签管理控制器
 * 提供标签的创建、查询、更新、删除和关联动态等功能
 */
@Tag(name = "动态标签管理", description = "提供标签的创建、查询、更新、删除和关联动态等功能")
@RestController
@RequestMapping("/api/post-tags")
public class PostTagController {
    
    @Autowired
    private PostTagService postTagService;
    
    @Autowired
    private PostService postService;
    
    /**
     * 获取所有标签
     */
    @Operation(summary = "获取所有标签", description = "获取系统中所有标签的列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping
    public Result<List<PostTag>> getAllTags() {
        return Result.success(postTagService.getAllTags());
    }
    
    /**
     * 创建新标签
     */
    @Operation(summary = "创建新标签", description = "创建一个新的标签，需要提供标签名称和描述")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "409", description = "标签名称已存在")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Result<PostTag> createTag(
            @Parameter(description = "标签信息", required = true) @RequestBody PostTag tag) {
        return Result.success(postTagService.createTag(tag.getName(), tag.getDescription()));
    }
    
    /**
     * 删除标签（仅管理员可操作）
     */
    @Operation(summary = "删除标签", description = "根据标签ID删除标签，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限删除标签"),
        @ApiResponse(responseCode = "404", description = "标签不存在")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> deleteTag(
            @Parameter(description = "标签ID", required = true) @PathVariable Long id) {
        postTagService.deleteTag(id);
        return Result.success(null);
    }
    
    /**
     * 更新标签
     */
    @Operation(summary = "更新标签", description = "根据标签ID更新标签信息，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限更新标签"),
        @ApiResponse(responseCode = "404", description = "标签不存在"),
        @ApiResponse(responseCode = "409", description = "新标签名称已存在")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<PostTag> updateTag(
            @Parameter(description = "标签ID", required = true) @PathVariable Long id,
            @Parameter(description = "更新的标签信息", required = true) @RequestBody PostTag tag) {
        return Result.success(postTagService.updateTag(id, tag));
    }
    
    /**
     * 为动态添加标签
     */
    @Operation(summary = "为动态添加标签", description = "为指定动态添加一个标签，如果标签不存在则创建新标签")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "添加成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限为动态添加标签"),
        @ApiResponse(responseCode = "404", description = "动态不存在")
    })
    @PostMapping("/{postId}/add")
    @PreAuthorize("hasRole('USER')")
    public Result<Post> addTagToPost(
            @Parameter(description = "动态ID", required = true) @PathVariable Long postId,
            @Parameter(description = "标签信息", required = true) @RequestBody Map<String, String> request) {
        String tagName = request.get("name");
        String description = request.get("description");
        
        // 先创建标签
        PostTag tag = postTagService.createTag(tagName, description);
        
        // 然后添加到动态
        return Result.success(postService.addTag(postId, tag.getId()));
    }
    
    /**
     * 从动态中移除标签
     */
    @Operation(summary = "从动态中移除标签", description = "从指定动态中移除一个标签")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "移除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限从动态中移除标签"),
        @ApiResponse(responseCode = "404", description = "动态或标签不存在")
    })
    @DeleteMapping("/{postId}/remove/{tagId}")
    @PreAuthorize("hasRole('USER')")
    public Result<Post> removeTagFromPost(
            @Parameter(description = "动态ID", required = true) @PathVariable Long postId,
            @Parameter(description = "标签ID", required = true) @PathVariable Long tagId) {
        return Result.success(postService.removeTag(postId, tagId));
    }
    
    /**
     * 获取动态的所有标签
     */
    @Operation(summary = "获取动态的所有标签", description = "获取指定动态关联的所有标签")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "动态不存在")
    })
    @GetMapping("/{postId}")
    public Result<Set<PostTag>> getPostTags(
            @Parameter(description = "动态ID", required = true) @PathVariable Long postId) {
        return Result.success(postService.getPostTags(postId));
    }
    
    /**
     * 根据标签查找动态
     */
    @Operation(summary = "根据标签查找动态", description = "获取关联了指定标签的所有动态")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "标签不存在")
    })
    @GetMapping("/posts/{tagId}")
    public Result<Page<Post>> getPostsByTag(
            @Parameter(description = "标签ID", required = true) @PathVariable Long tagId,
            @Parameter(description = "分页信息") Pageable pageable) {
        return Result.success(postService.findPostsByTag(tagId, pageable));
    }
    
    /**
     * 启用或禁用标签
     */
    @Operation(summary = "启用或禁用标签", description = "设置标签的启用或禁用状态，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "设置成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限设置标签状态"),
        @ApiResponse(responseCode = "404", description = "标签不存在")
    })
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<PostTag> setTagStatus(
            @Parameter(description = "标签ID", required = true) @PathVariable Long id,
            @Parameter(description = "状态信息", required = true) @RequestBody Map<String, Boolean> request) {
        Boolean enabled = request.get("enabled");
        return Result.success(postTagService.setTagEnabled(id, enabled)
            .orElseThrow(() -> new IllegalArgumentException("标签不存在")));
    }
    
    /**
     * 获取热门标签
     */
    @Operation(summary = "获取热门标签", description = "按热度降序返回前N个热门标签（默认10个）")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/hot")
    public Result<List<PostTag>> getHotTags(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        return Result.success(postTagService.getTopHotTags(limit));
    }
} 
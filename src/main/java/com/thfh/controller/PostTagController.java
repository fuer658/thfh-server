package com.thfh.controller;

import com.thfh.model.Post;
import com.thfh.model.PostTag;
import com.thfh.service.PostService;
import com.thfh.service.PostTagService;
import com.thfh.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags = "动态标签管理", description = "提供标签的创建、查询、更新、删除和关联动态等功能")
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
    @ApiOperation(value = "获取所有标签", notes = "获取系统中所有标签的列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping
    public Result<List<PostTag>> getAllTags() {
        return Result.success(postTagService.getAllTags());
    }
    
    /**
     * 创建新标签
     */
    @ApiOperation(value = "创建新标签", notes = "创建一个新的标签，需要提供标签名称和描述")
    @ApiResponses({
        @ApiResponse(code = 200, message = "创建成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 409, message = "标签名称已存在")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Result<PostTag> createTag(
            @ApiParam(value = "标签信息", required = true) @RequestBody PostTag tag) {
        return Result.success(postTagService.createTag(tag.getName(), tag.getDescription()));
    }
    
    /**
     * 删除标签（仅管理员可操作）
     */
    @ApiOperation(value = "删除标签", notes = "根据标签ID删除标签，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限删除标签"),
        @ApiResponse(code = 404, message = "标签不存在")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> deleteTag(
            @ApiParam(value = "标签ID", required = true) @PathVariable Long id) {
        postTagService.deleteTag(id);
        return Result.success(null);
    }
    
    /**
     * 更新标签
     */
    @ApiOperation(value = "更新标签", notes = "根据标签ID更新标签信息，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限更新标签"),
        @ApiResponse(code = 404, message = "标签不存在"),
        @ApiResponse(code = 409, message = "新标签名称已存在")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<PostTag> updateTag(
            @ApiParam(value = "标签ID", required = true) @PathVariable Long id,
            @ApiParam(value = "更新的标签信息", required = true) @RequestBody PostTag tag) {
        return Result.success(postTagService.updateTag(id, tag));
    }
    
    /**
     * 为动态添加标签
     */
    @ApiOperation(value = "为动态添加标签", notes = "为指定动态添加一个标签，如果标签不存在则创建新标签")
    @ApiResponses({
        @ApiResponse(code = 200, message = "添加成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限为动态添加标签"),
        @ApiResponse(code = 404, message = "动态不存在")
    })
    @PostMapping("/{postId}/add")
    @PreAuthorize("hasRole('USER')")
    public Result<Post> addTagToPost(
            @ApiParam(value = "动态ID", required = true) @PathVariable Long postId,
            @ApiParam(value = "标签信息", required = true) @RequestBody Map<String, String> request) {
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
    @ApiOperation(value = "从动态中移除标签", notes = "从指定动态中移除一个标签")
    @ApiResponses({
        @ApiResponse(code = 200, message = "移除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限从动态中移除标签"),
        @ApiResponse(code = 404, message = "动态或标签不存在")
    })
    @DeleteMapping("/{postId}/remove/{tagId}")
    @PreAuthorize("hasRole('USER')")
    public Result<Post> removeTagFromPost(
            @ApiParam(value = "动态ID", required = true) @PathVariable Long postId,
            @ApiParam(value = "标签ID", required = true) @PathVariable Long tagId) {
        return Result.success(postService.removeTag(postId, tagId));
    }
    
    /**
     * 获取动态的所有标签
     */
    @ApiOperation(value = "获取动态的所有标签", notes = "获取指定动态关联的所有标签")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "动态不存在")
    })
    @GetMapping("/{postId}")
    public Result<Set<PostTag>> getPostTags(
            @ApiParam(value = "动态ID", required = true) @PathVariable Long postId) {
        return Result.success(postService.getPostTags(postId));
    }
    
    /**
     * 根据标签查找动态
     */
    @ApiOperation(value = "根据标签查找动态", notes = "获取关联了指定标签的所有动态")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "标签不存在")
    })
    @GetMapping("/posts/{tagId}")
    public Result<Page<Post>> getPostsByTag(
            @ApiParam(value = "标签ID", required = true) @PathVariable Long tagId,
            @ApiParam(value = "分页信息") Pageable pageable) {
        return Result.success(postService.findPostsByTag(tagId, pageable));
    }
    
    /**
     * 启用或禁用标签
     */
    @ApiOperation(value = "启用或禁用标签", notes = "设置标签的启用或禁用状态，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(code = 200, message = "设置成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限设置标签状态"),
        @ApiResponse(code = 404, message = "标签不存在")
    })
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<PostTag> setTagStatus(
            @ApiParam(value = "标签ID", required = true) @PathVariable Long id,
            @ApiParam(value = "状态信息", required = true) @RequestBody Map<String, Boolean> request) {
        Boolean enabled = request.get("enabled");
        return Result.success(postTagService.setTagEnabled(id, enabled)
            .orElseThrow(() -> new IllegalArgumentException("标签不存在")));
    }
} 
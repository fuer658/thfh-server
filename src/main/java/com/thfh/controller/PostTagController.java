package com.thfh.controller;

import com.thfh.model.Post;
import com.thfh.model.PostTag;
import com.thfh.service.PostService;
import com.thfh.service.PostTagService;
import com.thfh.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @GetMapping
    public Result<List<PostTag>> getAllTags() {
        return Result.success(postTagService.getAllTags());
    }
    
    /**
     * 创建新标签
     */
    @PostMapping
    public Result<PostTag> createTag(@RequestBody PostTag tag) {
        return Result.success(postTagService.createTag(tag.getName(), tag.getDescription()));
    }
    
    /**
     * 删除标签
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        postTagService.deleteTag(id);
        return Result.success(null);
    }
    
    /**
     * 更新标签
     */
    @PutMapping("/{id}")
    public Result<PostTag> updateTag(@PathVariable Long id, @RequestBody PostTag tag) {
        return Result.success(postTagService.updateTag(id, tag));
    }
    
    /**
     * 为动态添加标签
     */
    @PostMapping("/{postId}/add")
    public Result<Post> addTagToPost(
            @PathVariable Long postId,
            @RequestBody Map<String, String> request) {
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
    @DeleteMapping("/{postId}/remove/{tagId}")
    public Result<Post> removeTagFromPost(
            @PathVariable Long postId,
            @PathVariable Long tagId) {
        return Result.success(postService.removeTag(postId, tagId));
    }
    
    /**
     * 获取动态的所有标签
     */
    @GetMapping("/{postId}")
    public Result<Set<PostTag>> getPostTags(@PathVariable Long postId) {
        return Result.success(postService.getPostTags(postId));
    }
    
    /**
     * 根据标签查找动态
     */
    @GetMapping("/posts/{tagId}")
    public Result<Page<Post>> getPostsByTag(
            @PathVariable Long tagId,
            Pageable pageable) {
        return Result.success(postService.findPostsByTag(tagId, pageable));
    }
    
    /**
     * 启用或禁用标签
     */
    @PutMapping("/{id}/status")
    public Result<PostTag> setTagStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        Boolean enabled = request.get("enabled");
        return Result.success(postTagService.setTagEnabled(id, enabled)
            .orElseThrow(() -> new IllegalArgumentException("标签不存在")));
    }
} 
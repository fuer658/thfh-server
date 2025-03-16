package com.thfh.controller;

import com.thfh.model.Post;
import com.thfh.model.PostTag;
import com.thfh.service.PostService;
import com.thfh.service.PostTagService;
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
     * 创建新标签
     */
    @PostMapping
    public ResponseEntity<PostTag> createTag(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.get("description");
        return ResponseEntity.ok(postTagService.createTag(name, description));
    }
    
    /**
     * 获取所有标签
     */
    @GetMapping
    public ResponseEntity<List<PostTag>> getAllTags() {
        return ResponseEntity.ok(postTagService.getAllTags());
    }
    
    /**
     * 为动态添加标签
     */
    @PostMapping("/{postId}/add")
    public ResponseEntity<Post> addTagToPost(
            @PathVariable Long postId,
            @RequestBody Map<String, String> request) {
        String tagName = request.get("name");
        String description = request.get("description");
        return ResponseEntity.ok(postService.addTag(postId, tagName, description));
    }
    
    /**
     * 从动态中移除标签
     */
    @DeleteMapping("/{postId}/remove/{tagId}")
    public ResponseEntity<Post> removeTagFromPost(
            @PathVariable Long postId,
            @PathVariable Long tagId) {
        return ResponseEntity.ok(postService.removeTag(postId, tagId));
    }
    
    /**
     * 获取动态的所有标签
     */
    @GetMapping("/{postId}")
    public ResponseEntity<Set<PostTag>> getPostTags(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostTags(postId));
    }
    
    /**
     * 根据标签查找动态
     */
    @GetMapping("/posts/{tagId}")
    public ResponseEntity<Page<Post>> getPostsByTag(
            @PathVariable Long tagId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.findPostsByTag(tagId, pageable));
    }
    
    /**
     * 更新标签
     */
    @PutMapping("/{id}")
    public ResponseEntity<PostTag> updateTag(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.get("description");
        return ResponseEntity.ok(postTagService.updateTag(id, name, description).orElseThrow(
            () -> new IllegalArgumentException("标签不存在")));
    }
    
    /**
     * 启用或禁用标签
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<PostTag> setTagStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        Boolean enabled = request.get("enabled");
        return ResponseEntity.ok(postTagService.setTagEnabled(id, enabled).orElseThrow(
            () -> new IllegalArgumentException("标签不存在")));
    }
} 
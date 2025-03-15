package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.PostDTO;
import com.thfh.model.Post;
import com.thfh.model.PostComment;
import com.thfh.model.User;
import com.thfh.service.PostService;
import com.thfh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    /**
     * 发布动态
     */
    @PostMapping
    public Result<Post> createPost(@RequestBody Post post) {
        return Result.success(postService.createPost(post));
    }

    /**
     * 获取动态详情
     */
    @GetMapping("/{postId}")
    public Result<Post> getPost(@PathVariable Long postId) {
        return Result.success(postService.getPost(postId));
    }

    /**
     * 获取用户动态列表
     */
    @GetMapping("/user/{userId}")
    public Result<Page<Post>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(postService.getUserPosts(userId, pageRequest));
    }

    /**
     * 点赞动态
     */
    @PostMapping("/{postId}/like")
    public Result<Void> likePost(@PathVariable Long postId) {
        postService.likePost(postId);
        return Result.success(null);
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/{postId}/like")
    public Result<Void> unlikePost(@PathVariable Long postId) {
        postService.unlikePost(postId);
        return Result.success(null);
    }

    /**
     * 评论动态
     */
    @PostMapping("/{postId}/comments")
    public Result<PostComment> commentPost(
            @PathVariable Long postId,
            @RequestBody String content) {
        return Result.success(postService.commentPost(postId, content));
    }

    /**
     * 获取动态评论列表
     */
    @GetMapping("/{postId}/comments")
    public Result<Page<PostComment>> getPostComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(postService.getPostComments(postId, pageRequest));
    }

    /**
     * 转发动态
     */
    @PostMapping("/{postId}/share")
    public Result<Void> sharePost(@PathVariable Long postId) {
        postService.sharePost(postId);
        return Result.success(null);
    }

    /**
     * 删除动态
     */
    @DeleteMapping("/{postId}")
    public Result<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return Result.success(null);
    }

    /**
     * 更新动态
     */
    @PutMapping("/{postId}")
    public Result<Post> updatePost(
            @PathVariable Long postId,
            @RequestBody Post post) {
        return Result.success(postService.updatePost(postId, post));
    }

    /**
     * 检查用户是否已点赞动态
     */
    @GetMapping("/{postId}/isLiked")
    public Result<Boolean> isLiked(@PathVariable Long postId) {
        User currentUser = userService.getCurrentUser();
        return Result.success(postService.isLiked(postId, currentUser.getId()));
    }

    /**
     * 获取所有动态列表
     */
    @GetMapping
    public Result<Page<PostDTO>> getAllPosts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String userName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(postService.getAllPosts(title, userName, pageRequest));
    }

    /**
     * 获取关注用户的动态列表
     */
    @GetMapping("/following")
    public Result<Page<Post>> getFollowingPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(postService.getFollowingPosts(pageRequest));
    }

    /**
     * 管理员以指定用户身份发布动态
     * @param userId 用户ID
     * @param post 动态内容
     * @return 创建的动态
     */
    @PostMapping("/admin/{userId}")
    public Result<Post> createPostByAdmin(
            @PathVariable Long userId,
            @RequestBody Post post) {
        return Result.success(postService.createPostByAdmin(post, userId));
    }

    /**
     * 管理员删除动态
     * @param postId 动态ID
     * @return 操作结果
     */
    @DeleteMapping("/admin/{postId}")
    public Result<Void> deletePostByAdmin(@PathVariable Long postId) {
        postService.deletePostByAdmin(postId);
        return Result.success(null);
    }
}
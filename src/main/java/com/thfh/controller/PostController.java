package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.CommentRequest;
import com.thfh.dto.PostDTO;
import com.thfh.model.Post;
import com.thfh.model.PostComment;
import com.thfh.model.User;
import com.thfh.dto.PostCommentDTO;
import com.thfh.service.PostService;
import com.thfh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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
    @PreAuthorize("hasRole('USER')")
    public Result<Post> createPost(@RequestBody Post post) {
        // 如果有标签ID列表，为动态添加标签
        Set<Long> tagIds = post.getTagIds();
        Post createdPost = postService.createPost(post);

        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                postService.addTag(createdPost.getId(), tagId);
            }
        }

        return Result.success(createdPost);
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
            @RequestBody CommentRequest request) {
        return Result.success(postService.commentPost(
            postId,
            request.getContent(),
            request.getParentId()
        ));
    }

    /**
     * 获取动态评论列表（树状结构）
     */
    @GetMapping("/{postId}/comments/tree")
    public Result<Page<PostCommentDTO>> getPostCommentTree(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(postService.getPostCommentTree(postId, pageRequest));
    }

    /**
     * 获取动态评论列表（扁平结构）
     */
    @GetMapping("/{postId}/comments")
    public Result<Page<PostCommentDTO>> getPostComments(
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
    public Result<Page<PostDTO>> getFollowingPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(postService.getFollowingPostsWithUserInfo(pageRequest));
    }

    /**
     * 管理员以指定用户身份发布动态
     * @param userId 用户ID
     * @param post 动态内容
     * @return 创建的动态
     */
    @PostMapping("/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Post> createPostByAdmin(@PathVariable Long userId, @RequestBody Post post) {
        // 如果有标签ID列表，为动态添加标签
        Set<Long> tagIds = post.getTagIds();
        Post createdPost = postService.createPost(post);

        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                postService.addTag(createdPost.getId(), tagId);
            }
        }

        return Result.success(createdPost);
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

    /**
     * 获取用户点赞的动态列表
     */
    @GetMapping("/likes")
    public Result<Page<Post>> getUserLikedPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        User currentUser = userService.getCurrentUser();
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(postService.getUserLikedPosts(currentUser.getId(), pageRequest));
    }

    /**
     * 管理员以指定用户身份评论动态
     * @param postId 动态ID
     * @param userId 用户ID，表示以哪个用户的身份发布评论
     * @param request 评论请求，包含评论内容和父评论ID
     * @return 创建的评论
     */
    @PostMapping("/admin/{userId}/posts/{postId}/comments")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PostComment> commentPostByAdmin(
            @PathVariable Long postId,
            @PathVariable Long userId,
            @RequestBody CommentRequest request) {
        return Result.success(postService.commentPostByAdmin(
            postId,
            request.getContent(),
            request.getParentId(),
            userId
        ));
    }

    /**
     * 管理员删除评论
     * @param commentId 评论ID
     * @return 操作结果
     */
    @DeleteMapping("/admin/comments/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteCommentByAdmin(@PathVariable Long commentId) {
        postService.deleteCommentByAdmin(commentId);
        return Result.success(null);
    }

    /**
     * 点赞评论
     * @param commentId 评论ID
     * @return 更新后的点赞数
     */
    @PostMapping("/comments/{commentId}/like")
    public Result<Integer> likeComment(@PathVariable Long commentId) {
        int likeCount = postService.likeComment(commentId);
        return Result.success(likeCount);
    }

    /**
     * 取消点赞评论
     * @param commentId 评论ID
     * @return 更新后的点赞数
     */
    @DeleteMapping("/comments/{commentId}/like")
    public Result<Integer> unlikeComment(@PathVariable Long commentId) {
        int likeCount = postService.unlikeComment(commentId);
        return Result.success(likeCount);
    }

    /**
     * 检查用户是否已点赞评论
     * @param commentId 评论ID
     * @return 是否已点赞
     */
    @GetMapping("/comments/{commentId}/isLiked")
    public Result<Boolean> isCommentLiked(@PathVariable Long commentId) {
        User currentUser = userService.getCurrentUser();
        return Result.success(postService.isCommentLiked(commentId, currentUser.getId()));
    }

    /**
     * 管理员以指定用户身份点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 更新后的点赞数
     */
    @PostMapping("/admin/{userId}/comments/{commentId}/like")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Integer> likeCommentByAdmin(
            @PathVariable Long commentId,
            @PathVariable Long userId) {
        int likeCount = postService.likeCommentByAdmin(commentId, userId);
        return Result.success(likeCount);
    }
}
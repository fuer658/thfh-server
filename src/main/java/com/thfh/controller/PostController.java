package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.CommentRequest;
import com.thfh.dto.PostDTO;
import com.thfh.model.Post;
import com.thfh.model.User;
import com.thfh.dto.PostCommentDTO;
import com.thfh.service.PostService;
import com.thfh.service.UserService;
import com.thfh.service.PostRecommendationService;
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
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;
import com.thfh.dto.PostReportRequest;

/**
 * 动态管理控制器
 * 提供动态的发布、查询、评论、点赞、分享和删除等功能
 */
@Tag(name = "动态管理", description = "提供动态的发布、查询、评论、点赞、分享和删除等功能")
@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostRecommendationService postRecommendationService;

    /**
     * 验证排序方向参数
     * @param direction 排序方向字符串
     * @return 验证后的排序方向
     */
    private Sort.Direction validateSortDirection(String direction) {
        try {
            return Sort.Direction.fromString(direction);
        } catch (Exception e) {
            return Sort.Direction.DESC; // 默认降序
        }
    }

    /**
     * 发布动态
     */
    @Operation(summary = "发布动态", description = "用户发布新动态，可以包含文本内容、图片和标签等")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "发布成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Result<PostDTO> createPost(
            @Parameter(description = "动态信息", required = true) @Valid @RequestBody Post post) {
        Post createdPost = postService.createPost(post);
        return Result.success(postService.toPostDTO(createdPost));
    }

    /**
     * 获取动态详情
     */
    @Operation(summary = "获取动态详情", description = "根据动态ID获取动态的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "动态不存在")
    })
    @GetMapping("/{postId}")
    public Result<PostDTO> getPost(
            @Parameter(description = "动态ID", required = true) @PathVariable Long postId) {
        return Result.success(postService.getPostDTO(postId));
    }

    /**
     * 获取用户动态列表
     */
    @Operation(summary = "获取用户动态列表", description = "获取指定用户发布的所有动态")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/user/{userId}")
    public Result<Page<PostDTO>> getUserPosts(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段，支持createTime、updateTime、likeCount、commentCount、shareCount、viewCount、title") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向，支持ASC或DESC") @RequestParam(defaultValue = "DESC") String direction) {
        Sort.Direction sortDirection = validateSortDirection(direction);
        String validatedSortBy = postService.validateSortField(sortBy);
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(sortDirection, validatedSortBy));
        return Result.success(postService.getUserPostsDTO(userId, pageRequest));
    }

    /**
     * 点赞动态
     */
    @Operation(summary = "点赞动态", description = "用户对动态进行点赞")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "点赞成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "动态不存在"),
        @ApiResponse(responseCode = "409", description = "已经点赞过该动态")
    })
    @PostMapping("/{postId}/like")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> likePost(
            @Parameter(description = "动态ID", required = true) @PathVariable Long postId) {
        postService.likePost(postId);
        return Result.success(null);
    }

    /**
     * 取消点赞
     */
    @Operation(summary = "取消点赞", description = "用户取消对动态的点赞")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "取消成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "动态不存在或未点赞")
    })
    @DeleteMapping("/{postId}/like")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> unlikePost(
            @Parameter(description = "动态ID", required = true) @PathVariable Long postId) {
        postService.unlikePost(postId);
        return Result.success(null);
    }

    /**
     * 评论动态
     */
    @Operation(summary = "评论动态", description = "用户对动态发表评论，支持回复其他评论")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "评论成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "动态不存在或父评论不存在")
    })
    @PostMapping("/{postId}/comments")
    @PreAuthorize("hasRole('USER')")
    public Result<PostCommentDTO> commentPost(
            @Parameter(description = "动态ID", required = true) @PathVariable Long postId,
            @Parameter(description = "评论信息", required = true) @Valid @RequestBody CommentRequest request) {
        return Result.success(postService.commentPostDTO(
            postId,
            request.getContent(),
            request.getParentId()
        ));
    }

    /**
     * 获取动态评论列表（树状结构）
     */
    @Operation(summary = "获取动态评论列表（树状结构）", description = "获取指定动态的评论列表，以树状结构返回评论和回复")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "动态不存在")
    })
    @GetMapping("/{postId}/comments/tree")
    public Result<Page<PostCommentDTO>> getPostCommentTree(
            @Parameter(description = "动态ID", required = true) @PathVariable Long postId,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(postService.getPostCommentTree(postId, pageRequest));
    }

    /**
     * 获取动态评论列表（扁平结构）
     */
    @Operation(summary = "获取动态评论列表（扁平结构）", description = "获取指定动态的评论列表，以扁平结构返回所有评论")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "动态不存在")
    })
    @GetMapping("/{postId}/comments")
    public Result<Page<PostCommentDTO>> getPostComments(
            @Parameter(description = "动态ID", required = true) @PathVariable Long postId,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(postService.getPostComments(postId, pageRequest));
    }

    /**
     * 转发动态
     */
    @Operation(summary = "转发动态", description = "用户转发动态到自己的动态列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "转发成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "动态不存在")
    })
    @PostMapping("/{postId}/share")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> sharePost(
            @Parameter(description = "动态ID", required = true) @PathVariable Long postId) {
        postService.sharePost(postId);
        return Result.success(null);
    }

    /**
     * 删除动态
     */
    @Operation(summary = "删除动态", description = "用户删除自己发布的动态")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限删除该动态"),
        @ApiResponse(responseCode = "404", description = "动态不存在")
    })
    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> deletePost(
            @Parameter(description = "动态ID", required = true) @PathVariable Long postId) {
        postService.deletePost(postId);
        return Result.success(null);
    }

    /**
     * 更新动态
     */
    @Operation(summary = "更新动态", description = "用户更新自己发布的动态内容")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限更新该动态"),
        @ApiResponse(responseCode = "404", description = "动态不存在")
    })
    @PutMapping("/{postId}")
    @PreAuthorize("hasRole('USER')")
    public Result<PostDTO> updatePost(
            @Parameter(description = "动态ID", required = true) @PathVariable Long postId,
            @Parameter(description = "更新的动态信息", required = true) @Valid @RequestBody Post post) {
        Post updatedPost = postService.updatePost(postId, post);
        return Result.success(postService.toPostDTO(updatedPost));
    }

    /**
     * 检查用户是否已点赞动态
     */
    @Operation(summary = "检查用户是否已点赞动态", description = "检查当前登录用户是否已经对指定动态点赞")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "检查成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "动态不存在")
    })
    @GetMapping("/{postId}/isLiked")
    @PreAuthorize("hasRole('USER')")
    public Result<Boolean> isLiked(
            @Parameter(description = "动态ID", required = true) @PathVariable Long postId) {
        User currentUser = userService.getCurrentUser();
        return Result.success(postService.isLiked(postId, currentUser.getId()));
    }

    /**
     * 获取所有动态列表
     */
    @Operation(summary = "获取所有动态列表", description = "获取系统中的所有动态，支持按标题和用户名筛选，默认返回推荐动态")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping
    public Result<Page<PostDTO>> getAllPosts(
            @Parameter(description = "动态标题，用于筛选") @RequestParam(required = false) String title,
            @Parameter(description = "用户名，用于筛选") @RequestParam(required = false) String userName,
            @Parameter(description = "是否使用推荐算法，默认为true") @RequestParam(defaultValue = "true") boolean useRecommendation,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段，支持createTime、updateTime、likeCount、commentCount、shareCount、viewCount、title") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向，支持ASC或DESC") @RequestParam(defaultValue = "DESC") String direction) {
        
        // 如果指定了标题或用户名作为筛选条件，或者明确不使用推荐，则使用普通查询
        if ((title != null && !title.isEmpty()) || (userName != null && !userName.isEmpty()) || !useRecommendation) {
            Sort.Direction sortDirection = validateSortDirection(direction);
            String validatedSortBy = postService.validateSortField(sortBy);
            PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(sortDirection, validatedSortBy));
            return Result.success(postService.getAllPosts(title, userName, pageRequest));
        } else {
            // 否则使用推荐算法获取动态
            PageRequest pageRequest = PageRequest.of(page - 1, size);
            return Result.success(postRecommendationService.getRecommendedPosts(pageRequest));
        }
    }

    /**
     * 获取推荐动态列表
     */
    @Operation(summary = "获取推荐动态列表", description = "根据用户兴趣、浏览历史和行为获取个性化推荐动态")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/recommended")
    public Result<Page<PostDTO>> getRecommendedPosts(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return Result.success(postRecommendationService.getRecommendedPosts(pageRequest));
    }

    /**
     * 获取关注用户的动态列表
     */
    @Operation(summary = "获取关注用户的动态列表", description = "获取当前用户关注的所有用户发布的动态")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/following")
    public Result<Page<PostDTO>> getFollowingPosts(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段，支持createTime、updateTime、likeCount、commentCount、shareCount、viewCount、title") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向，支持ASC或DESC") @RequestParam(defaultValue = "DESC") String direction) {
        Sort.Direction sortDirection = validateSortDirection(direction);
        String validatedSortBy = postService.validateSortField(sortBy);
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(sortDirection, validatedSortBy));
        return Result.success(postService.getFollowingPostsWithUserInfo(pageRequest));
    }

    /**
     * 管理员以指定用户身份发布动态
     * @param userId 用户ID
     * @param post 动态内容
     * @return 创建的动态
     */
    @Operation(summary = "管理员以指定用户身份发布动态", description = "管理员可以以指定用户的身份发布动态，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "发布成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有管理员权限"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PostMapping("/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PostDTO> createPostByAdmin(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "动态内容", required = true) @Valid @RequestBody Post post) {
        Post createdPost = postService.createPost(post);
        return Result.success(postService.toPostDTO(createdPost));
    }

    /**
     * 管理员删除动态
     * @param postId 动态ID
     * @return 操作结果
     */
    @Operation(summary = "管理员删除动态", description = "管理员可以删除任何用户的动态，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有管理员权限"),
        @ApiResponse(responseCode = "404", description = "动态不存在")
    })
    @DeleteMapping("/admin/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deletePostByAdmin(
            @Parameter(description = "动态ID", required = true) @PathVariable Long postId) {
        postService.deletePostByAdmin(postId);
        return Result.success(null);
    }

    /**
     * 获取用户点赞的动态列表
     */
    @Operation(summary = "获取用户点赞的动态列表", description = "获取当前登录用户点赞过的所有动态")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/likes")
    @PreAuthorize("hasRole('USER')")
    public Result<Page<PostDTO>> getUserLikedPosts(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段，支持createTime、updateTime、likeCount、commentCount、shareCount、viewCount、title") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向，支持ASC或DESC") @RequestParam(defaultValue = "DESC") String direction) {
        User currentUser = userService.getCurrentUser();
        Sort.Direction sortDirection = validateSortDirection(direction);
        String validatedSortBy = postService.validateSortField(sortBy);
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(sortDirection, validatedSortBy));
        return Result.success(postService.getUserLikedPostsDTO(currentUser.getId(), pageRequest));
    }

    /**
     * 管理员以指定用户身份评论动态
     * @param postId 动态ID
     * @param userId 用户ID，表示以哪个用户的身份发布评论
     * @param request 评论请求，包含评论内容和父评论ID
     * @return 创建的评论
     */
    @Operation(summary = "管理员以指定用户身份评论动态", description = "管理员可以以指定用户的身份对动态发表评论，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "评论成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有管理员权限"),
        @ApiResponse(responseCode = "404", description = "动态、用户或父评论不存在")
    })
    @PostMapping("/admin/{userId}/posts/{postId}/comments")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PostCommentDTO> commentPostByAdmin(
            @Parameter(description = "动态ID", required = true) @PathVariable Long postId,
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "评论请求", required = true) @Valid @RequestBody CommentRequest request) {
        return Result.success(postService.commentPostByAdminDTO(
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
    @Operation(summary = "管理员删除评论", description = "管理员可以删除任何用户的评论，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有管理员权限"),
        @ApiResponse(responseCode = "404", description = "评论不存在")
    })
    @DeleteMapping("/admin/comments/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteCommentByAdmin(
            @Parameter(description = "评论ID", required = true) @PathVariable Long commentId) {
        postService.deleteCommentByAdmin(commentId);
        return Result.success(null);
    }

    /**
     * 点赞评论
     * @param commentId 评论ID
     * @return 更新后的点赞数
     */
    @Operation(summary = "点赞评论", description = "用户对评论进行点赞")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "点赞成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "评论不存在"),
        @ApiResponse(responseCode = "409", description = "已经点赞过该评论")
    })
    @PostMapping("/comments/{commentId}/like")
    @PreAuthorize("hasRole('USER')")
    public Result<Integer> likeComment(
            @Parameter(description = "评论ID", required = true) @PathVariable Long commentId) {
        return Result.success(postService.likeComment(commentId));
    }

    /**
     * 取消评论点赞
     * @param commentId 评论ID
     * @return 更新后的点赞数
     */
    @Operation(summary = "取消评论点赞", description = "用户取消对评论的点赞")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "取消成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "评论不存在或未点赞")
    })
    @DeleteMapping("/comments/{commentId}/like")
    @PreAuthorize("hasRole('USER')")
    public Result<Integer> unlikeComment(
            @Parameter(description = "评论ID", required = true) @PathVariable Long commentId) {
        return Result.success(postService.unlikeComment(commentId));
    }

    /**
     * 检查用户是否已点赞评论
     * @param commentId 评论ID
     * @return 是否已点赞
     */
    @Operation(summary = "检查用户是否已点赞评论", description = "检查当前登录用户是否已经对指定评论点赞")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "检查成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "评论不存在")
    })
    @GetMapping("/comments/{commentId}/isLiked")
    @PreAuthorize("hasRole('USER')")
    public Result<Boolean> isCommentLiked(
            @Parameter(description = "评论ID", required = true) @PathVariable Long commentId) {
        User currentUser = userService.getCurrentUser();
        return Result.success(postService.isCommentLiked(commentId, currentUser.getId()));
    }

    /**
     * 管理员以指定用户身份点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 更新后的点赞数
     */
    @Operation(summary = "管理员以指定用户身份点赞评论", description = "管理员可以以指定用户的身份对评论进行点赞，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "点赞成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有管理员权限"),
        @ApiResponse(responseCode = "404", description = "评论或用户不存在"),
        @ApiResponse(responseCode = "409", description = "该用户已经点赞过该评论")
    })
    @PostMapping("/admin/{userId}/comments/{commentId}/like")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Integer> likeCommentByAdmin(
            @Parameter(description = "评论ID", required = true) @PathVariable Long commentId,
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        return Result.success(postService.likeCommentByAdmin(commentId, userId));
    }

    /**
     * 删除评论
     * @param commentId 评论ID
     * @return 操作结果
     */
    @Operation(summary = "删除评论", description = "用户删除自己发布的评论")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限删除该评论"),
        @ApiResponse(responseCode = "404", description = "评论不存在")
    })
    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> deleteComment(
            @Parameter(description = "评论ID", required = true) @PathVariable Long commentId) {
        postService.deleteComment(commentId);
        return Result.success(null);
    }

    /**
     * 举报动态
     */
    @Operation(summary = "举报动态", description = "用户举报动态，需填写举报原因和描述")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "举报成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "动态不存在")
    })
    @PostMapping("/{postId}/report")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> reportPost(
            @Parameter(description = "动态ID", required = true) @PathVariable Long postId,
            @Parameter(description = "举报信息", required = true) @Valid @RequestBody PostReportRequest request) {
        postService.reportPost(postId, request);
        return Result.success(null, "举报成功");
    }

    /**
     * 管理员修复所有帖子评论计数
     */
    @GetMapping("/admin/fix-comment-counts")
    @ResponseStatus(HttpStatus.OK)
    public Result<Integer> fixCommentCounts() {
        int fixedCount = postService.fixAllPostsCommentCount();
        return Result.success(fixedCount);
    }

    /**
     * 根据标签名称获取动态列表
     */
    @Operation(summary = "根据标签名称获取动态列表", description = "获取包含指定标签名称的所有动态")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/tag/name")
    public Result<Page<PostDTO>> getPostsByTagName(
            @Parameter(description = "标签名称", required = true) @RequestParam String tagName,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段，支持createTime、updateTime、likeCount、commentCount、shareCount、viewCount、title") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向，支持ASC或DESC") @RequestParam(defaultValue = "DESC") String direction) {
        Sort.Direction sortDirection = validateSortDirection(direction);
        String validatedSortBy = postService.validateSortField(sortBy);
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(sortDirection, validatedSortBy));
        return Result.success(postService.findPostsByTagNameDTO(tagName, pageRequest));
    }

    /**
     * 搜索动态
     */
    @Operation(summary = "搜索动态", description = "根据多种条件搜索动态，支持关键字、内容、标签、时间范围等条件组合搜索")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "搜索成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/search")
    public Result<Page<PostDTO>> searchPosts(
            @Parameter(description = "标题关键字") @RequestParam(required = false) String title,
            @Parameter(description = "内容关键字") @RequestParam(required = false) String content,
            @Parameter(description = "用户名关键字") @RequestParam(required = false) String userName,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "标签名称") @RequestParam(required = false) String tagName,
            @Parameter(description = "标签ID") @RequestParam(required = false) Long tagId,
            @Parameter(description = "最小点赞数") @RequestParam(required = false) Integer minLikes,
            @Parameter(description = "最小评论数") @RequestParam(required = false) Integer minComments,
            @Parameter(description = "开始时间，格式：yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间，格式：yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) String endTime,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段，支持createTime、updateTime、likeCount、commentCount、shareCount、viewCount、title") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向，支持ASC或DESC") @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = validateSortDirection(direction);
        String validatedSortBy = postService.validateSortField(sortBy);
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(sortDirection, validatedSortBy));
        
        return Result.success(postService.searchPosts(title, content, userName, userId, 
                tagName, tagId, minLikes, minComments, startTime, endTime, pageRequest));
    }
}
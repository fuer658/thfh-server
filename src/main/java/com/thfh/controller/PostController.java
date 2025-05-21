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
import org.springframework.http.HttpStatus;

import javax.validation.Valid;
import com.thfh.dto.PostReportRequest;

/**
 * 动态管理控制器
 * 提供动态的发布、查询、评论、点赞、分享和删除等功能
 */
@Api(tags = "动态管理", description = "提供动态的发布、查询、评论、点赞、分享和删除等功能")
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
    @ApiOperation(value = "发布动态", notes = "用户发布新动态，可以包含文本内容、图片和标签等")
    @ApiResponses({
        @ApiResponse(code = 200, message = "发布成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Result<PostDTO> createPost(
            @ApiParam(value = "动态信息", required = true) @Valid @RequestBody Post post) {
        Post createdPost = postService.createPost(post);
        return Result.success(postService.toPostDTO(createdPost));
    }

    /**
     * 获取动态详情
     */
    @ApiOperation(value = "获取动态详情", notes = "根据动态ID获取动态的详细信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "动态不存在")
    })
    @GetMapping("/{postId}")
    public Result<PostDTO> getPost(
            @ApiParam(value = "动态ID", required = true) @PathVariable Long postId) {
        return Result.success(postService.getPostDTO(postId));
    }

    /**
     * 获取用户动态列表
     */
    @ApiOperation(value = "获取用户动态列表", notes = "获取指定用户发布的所有动态")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @GetMapping("/user/{userId}")
    public Result<Page<PostDTO>> getUserPosts(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size,
            @ApiParam(value = "排序字段，支持createTime、updateTime、likeCount、commentCount、shareCount、viewCount、title", defaultValue = "createTime") @RequestParam(defaultValue = "createTime") String sortBy,
            @ApiParam(value = "排序方向，支持ASC或DESC", defaultValue = "DESC") @RequestParam(defaultValue = "DESC") String direction) {
        Sort.Direction sortDirection = validateSortDirection(direction);
        String validatedSortBy = postService.validateSortField(sortBy);
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(sortDirection, validatedSortBy));
        return Result.success(postService.getUserPostsDTO(userId, pageRequest));
    }

    /**
     * 点赞动态
     */
    @ApiOperation(value = "点赞动态", notes = "用户对动态进行点赞")
    @ApiResponses({
        @ApiResponse(code = 200, message = "点赞成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "动态不存在"),
        @ApiResponse(code = 409, message = "已经点赞过该动态")
    })
    @PostMapping("/{postId}/like")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> likePost(
            @ApiParam(value = "动态ID", required = true) @PathVariable Long postId) {
        postService.likePost(postId);
        return Result.success(null);
    }

    /**
     * 取消点赞
     */
    @ApiOperation(value = "取消点赞", notes = "用户取消对动态的点赞")
    @ApiResponses({
        @ApiResponse(code = 200, message = "取消成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "动态不存在或未点赞")
    })
    @DeleteMapping("/{postId}/like")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> unlikePost(
            @ApiParam(value = "动态ID", required = true) @PathVariable Long postId) {
        postService.unlikePost(postId);
        return Result.success(null);
    }

    /**
     * 评论动态
     */
    @ApiOperation(value = "评论动态", notes = "用户对动态发表评论，支持回复其他评论")
    @ApiResponses({
        @ApiResponse(code = 200, message = "评论成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "动态不存在或父评论不存在")
    })
    @PostMapping("/{postId}/comments")
    @PreAuthorize("hasRole('USER')")
    public Result<PostCommentDTO> commentPost(
            @ApiParam(value = "动态ID", required = true) @PathVariable Long postId,
            @ApiParam(value = "评论信息", required = true) @Valid @RequestBody CommentRequest request) {
        return Result.success(postService.commentPostDTO(
            postId,
            request.getContent(),
            request.getParentId()
        ));
    }

    /**
     * 获取动态评论列表（树状结构）
     */
    @ApiOperation(value = "获取动态评论列表（树状结构）", notes = "获取指定动态的评论列表，以树状结构返回评论和回复")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "动态不存在")
    })
    @GetMapping("/{postId}/comments/tree")
    public Result<Page<PostCommentDTO>> getPostCommentTree(
            @ApiParam(value = "动态ID", required = true) @PathVariable Long postId,
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(postService.getPostCommentTree(postId, pageRequest));
    }

    /**
     * 获取动态评论列表（扁平结构）
     */
    @ApiOperation(value = "获取动态评论列表（扁平结构）", notes = "获取指定动态的评论列表，以扁平结构返回所有评论")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "动态不存在")
    })
    @GetMapping("/{postId}/comments")
    public Result<Page<PostCommentDTO>> getPostComments(
            @ApiParam(value = "动态ID", required = true) @PathVariable Long postId,
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(postService.getPostComments(postId, pageRequest));
    }

    /**
     * 转发动态
     */
    @ApiOperation(value = "转发动态", notes = "用户转发动态到自己的动态列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "转发成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "动态不存在")
    })
    @PostMapping("/{postId}/share")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> sharePost(
            @ApiParam(value = "动态ID", required = true) @PathVariable Long postId) {
        postService.sharePost(postId);
        return Result.success(null);
    }

    /**
     * 删除动态
     */
    @ApiOperation(value = "删除动态", notes = "用户删除自己发布的动态")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限删除该动态"),
        @ApiResponse(code = 404, message = "动态不存在")
    })
    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> deletePost(
            @ApiParam(value = "动态ID", required = true) @PathVariable Long postId) {
        postService.deletePost(postId);
        return Result.success(null);
    }

    /**
     * 更新动态
     */
    @ApiOperation(value = "更新动态", notes = "用户更新自己发布的动态内容")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限更新该动态"),
        @ApiResponse(code = 404, message = "动态不存在")
    })
    @PutMapping("/{postId}")
    @PreAuthorize("hasRole('USER')")
    public Result<PostDTO> updatePost(
            @ApiParam(value = "动态ID", required = true) @PathVariable Long postId,
            @ApiParam(value = "更新的动态信息", required = true) @Valid @RequestBody Post post) {
        Post updatedPost = postService.updatePost(postId, post);
        return Result.success(postService.toPostDTO(updatedPost));
    }

    /**
     * 检查用户是否已点赞动态
     */
    @ApiOperation(value = "检查用户是否已点赞动态", notes = "检查当前登录用户是否已经对指定动态点赞")
    @ApiResponses({
        @ApiResponse(code = 200, message = "检查成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "动态不存在")
    })
    @GetMapping("/{postId}/isLiked")
    @PreAuthorize("hasRole('USER')")
    public Result<Boolean> isLiked(
            @ApiParam(value = "动态ID", required = true) @PathVariable Long postId) {
        User currentUser = userService.getCurrentUser();
        return Result.success(postService.isLiked(postId, currentUser.getId()));
    }

    /**
     * 获取所有动态列表
     */
    @ApiOperation(value = "获取所有动态列表", notes = "获取系统中的所有动态，支持按标题和用户名筛选，默认返回推荐动态")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping
    public Result<Page<PostDTO>> getAllPosts(
            @ApiParam(value = "动态标题，用于筛选") @RequestParam(required = false) String title,
            @ApiParam(value = "用户名，用于筛选") @RequestParam(required = false) String userName,
            @ApiParam(value = "是否使用推荐算法，默认为true") @RequestParam(defaultValue = "true") boolean useRecommendation,
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size,
            @ApiParam(value = "排序字段，支持createTime、updateTime、likeCount、commentCount、shareCount、viewCount、title", defaultValue = "createTime") @RequestParam(defaultValue = "createTime") String sortBy,
            @ApiParam(value = "排序方向，支持ASC或DESC", defaultValue = "DESC") @RequestParam(defaultValue = "DESC") String direction) {
        
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
    @ApiOperation(value = "获取推荐动态列表", notes = "根据用户兴趣、浏览历史和行为获取个性化推荐动态")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/recommended")
    public Result<Page<PostDTO>> getRecommendedPosts(
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return Result.success(postRecommendationService.getRecommendedPosts(pageRequest));
    }

    /**
     * 获取关注用户的动态列表
     */
    @ApiOperation(value = "获取关注用户的动态列表", notes = "获取当前用户关注的所有用户发布的动态")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/following")
    public Result<Page<PostDTO>> getFollowingPosts(
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size,
            @ApiParam(value = "排序字段，支持createTime、updateTime、likeCount、commentCount、shareCount、viewCount、title", defaultValue = "createTime") @RequestParam(defaultValue = "createTime") String sortBy,
            @ApiParam(value = "排序方向，支持ASC或DESC", defaultValue = "DESC") @RequestParam(defaultValue = "DESC") String direction) {
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
    @ApiOperation(value = "管理员以指定用户身份发布动态", notes = "管理员可以以指定用户的身份发布动态，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(code = 200, message = "发布成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有管理员权限"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @PostMapping("/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PostDTO> createPostByAdmin(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "动态内容", required = true) @Valid @RequestBody Post post) {
        Post createdPost = postService.createPost(post);
        return Result.success(postService.toPostDTO(createdPost));
    }

    /**
     * 管理员删除动态
     * @param postId 动态ID
     * @return 操作结果
     */
    @ApiOperation(value = "管理员删除动态", notes = "管理员可以删除任何用户的动态，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有管理员权限"),
        @ApiResponse(code = 404, message = "动态不存在")
    })
    @DeleteMapping("/admin/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deletePostByAdmin(
            @ApiParam(value = "动态ID", required = true) @PathVariable Long postId) {
        postService.deletePostByAdmin(postId);
        return Result.success(null);
    }

    /**
     * 获取用户点赞的动态列表
     */
    @ApiOperation(value = "获取用户点赞的动态列表", notes = "获取当前登录用户点赞过的所有动态")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/likes")
    @PreAuthorize("hasRole('USER')")
    public Result<Page<PostDTO>> getUserLikedPosts(
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size,
            @ApiParam(value = "排序字段，支持createTime、updateTime、likeCount、commentCount、shareCount、viewCount、title", defaultValue = "createTime") @RequestParam(defaultValue = "createTime") String sortBy,
            @ApiParam(value = "排序方向，支持ASC或DESC", defaultValue = "DESC") @RequestParam(defaultValue = "DESC") String direction) {
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
    @ApiOperation(value = "管理员以指定用户身份评论动态", notes = "管理员可以以指定用户的身份对动态发表评论，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(code = 200, message = "评论成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有管理员权限"),
        @ApiResponse(code = 404, message = "动态、用户或父评论不存在")
    })
    @PostMapping("/admin/{userId}/posts/{postId}/comments")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PostCommentDTO> commentPostByAdmin(
            @ApiParam(value = "动态ID", required = true) @PathVariable Long postId,
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "评论请求", required = true) @Valid @RequestBody CommentRequest request) {
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
    @ApiOperation(value = "管理员删除评论", notes = "管理员可以删除任何用户的评论，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有管理员权限"),
        @ApiResponse(code = 404, message = "评论不存在")
    })
    @DeleteMapping("/admin/comments/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteCommentByAdmin(
            @ApiParam(value = "评论ID", required = true) @PathVariable Long commentId) {
        postService.deleteCommentByAdmin(commentId);
        return Result.success(null);
    }

    /**
     * 点赞评论
     * @param commentId 评论ID
     * @return 更新后的点赞数
     */
    @ApiOperation(value = "点赞评论", notes = "用户对评论进行点赞")
    @ApiResponses({
        @ApiResponse(code = 200, message = "点赞成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "评论不存在"),
        @ApiResponse(code = 409, message = "已经点赞过该评论")
    })
    @PostMapping("/comments/{commentId}/like")
    @PreAuthorize("hasRole('USER')")
    public Result<Integer> likeComment(
            @ApiParam(value = "评论ID", required = true) @PathVariable Long commentId) {
        return Result.success(postService.likeComment(commentId));
    }

    /**
     * 取消评论点赞
     * @param commentId 评论ID
     * @return 更新后的点赞数
     */
    @ApiOperation(value = "取消评论点赞", notes = "用户取消对评论的点赞")
    @ApiResponses({
        @ApiResponse(code = 200, message = "取消成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "评论不存在或未点赞")
    })
    @DeleteMapping("/comments/{commentId}/like")
    @PreAuthorize("hasRole('USER')")
    public Result<Integer> unlikeComment(
            @ApiParam(value = "评论ID", required = true) @PathVariable Long commentId) {
        return Result.success(postService.unlikeComment(commentId));
    }

    /**
     * 检查用户是否已点赞评论
     * @param commentId 评论ID
     * @return 是否已点赞
     */
    @ApiOperation(value = "检查用户是否已点赞评论", notes = "检查当前登录用户是否已经对指定评论点赞")
    @ApiResponses({
        @ApiResponse(code = 200, message = "检查成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "评论不存在")
    })
    @GetMapping("/comments/{commentId}/isLiked")
    @PreAuthorize("hasRole('USER')")
    public Result<Boolean> isCommentLiked(
            @ApiParam(value = "评论ID", required = true) @PathVariable Long commentId) {
        User currentUser = userService.getCurrentUser();
        return Result.success(postService.isCommentLiked(commentId, currentUser.getId()));
    }

    /**
     * 管理员以指定用户身份点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 更新后的点赞数
     */
    @ApiOperation(value = "管理员以指定用户身份点赞评论", notes = "管理员可以以指定用户的身份对评论进行点赞，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(code = 200, message = "点赞成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有管理员权限"),
        @ApiResponse(code = 404, message = "评论或用户不存在"),
        @ApiResponse(code = 409, message = "该用户已经点赞过该评论")
    })
    @PostMapping("/admin/{userId}/comments/{commentId}/like")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Integer> likeCommentByAdmin(
            @ApiParam(value = "评论ID", required = true) @PathVariable Long commentId,
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        return Result.success(postService.likeCommentByAdmin(commentId, userId));
    }

    /**
     * 删除评论
     * @param commentId 评论ID
     * @return 操作结果
     */
    @ApiOperation(value = "删除评论", notes = "用户删除自己发布的评论")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限删除该评论"),
        @ApiResponse(code = 404, message = "评论不存在")
    })
    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> deleteComment(
            @ApiParam(value = "评论ID", required = true) @PathVariable Long commentId) {
        postService.deleteComment(commentId);
        return Result.success(null);
    }

    /**
     * 举报动态
     */
    @ApiOperation(value = "举报动态", notes = "用户举报动态，需填写举报原因和描述")
    @ApiResponses({
        @ApiResponse(code = 200, message = "举报成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "动态不存在")
    })
    @PostMapping("/{postId}/report")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> reportPost(
            @ApiParam(value = "动态ID", required = true) @PathVariable Long postId,
            @ApiParam(value = "举报信息", required = true) @Valid @RequestBody PostReportRequest request) {
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
    @ApiOperation(value = "根据标签名称获取动态列表", notes = "获取包含指定标签名称的所有动态")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/tag/name")
    public Result<Page<PostDTO>> getPostsByTagName(
            @ApiParam(value = "标签名称", required = true) @RequestParam String tagName,
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size,
            @ApiParam(value = "排序字段，支持createTime、updateTime、likeCount、commentCount、shareCount、viewCount、title", defaultValue = "createTime") @RequestParam(defaultValue = "createTime") String sortBy,
            @ApiParam(value = "排序方向，支持ASC或DESC", defaultValue = "DESC") @RequestParam(defaultValue = "DESC") String direction) {
        Sort.Direction sortDirection = validateSortDirection(direction);
        String validatedSortBy = postService.validateSortField(sortBy);
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(sortDirection, validatedSortBy));
        return Result.success(postService.findPostsByTagNameDTO(tagName, pageRequest));
    }

    /**
     * 搜索动态
     */
    @ApiOperation(value = "搜索动态", notes = "根据多种条件搜索动态，支持关键字、内容、标签、时间范围等条件组合搜索")
    @ApiResponses({
        @ApiResponse(code = 200, message = "搜索成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/search")
    public Result<Page<PostDTO>> searchPosts(
            @ApiParam(value = "标题关键字") @RequestParam(required = false) String title,
            @ApiParam(value = "内容关键字") @RequestParam(required = false) String content,
            @ApiParam(value = "用户名关键字") @RequestParam(required = false) String userName,
            @ApiParam(value = "用户ID") @RequestParam(required = false) Long userId,
            @ApiParam(value = "标签名称") @RequestParam(required = false) String tagName,
            @ApiParam(value = "标签ID") @RequestParam(required = false) Long tagId,
            @ApiParam(value = "最小点赞数") @RequestParam(required = false) Integer minLikes,
            @ApiParam(value = "最小评论数") @RequestParam(required = false) Integer minComments,
            @ApiParam(value = "开始时间，格式：yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) String startTime,
            @ApiParam(value = "结束时间，格式：yyyy-MM-dd HH:mm:ss") @RequestParam(required = false) String endTime,
            @ApiParam(value = "页码，从1开始", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") @RequestParam(defaultValue = "10") int size,
            @ApiParam(value = "排序字段，支持createTime、updateTime、likeCount、commentCount、shareCount、viewCount、title", defaultValue = "createTime") @RequestParam(defaultValue = "createTime") String sortBy,
            @ApiParam(value = "排序方向，支持ASC或DESC", defaultValue = "DESC") @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = validateSortDirection(direction);
        String validatedSortBy = postService.validateSortField(sortBy);
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(sortDirection, validatedSortBy));
        
        return Result.success(postService.searchPosts(title, content, userName, userId, 
                tagName, tagId, minLikes, minComments, startTime, endTime, pageRequest));
    }
}
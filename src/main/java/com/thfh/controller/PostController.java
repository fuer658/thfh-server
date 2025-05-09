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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

/**
 * 动态管理控制器
 * 提供动态的发布、查询、评论、点赞、分享和删除等功能
 */
@Api(tags = "动态管理", description = "提供动态的发布、查询、评论、点赞、分享和删除等功能")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PostController {
    
    private final PostService postService;
    private final UserService userService;

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
    public Result<Post> createPost(
            @ApiParam(value = "动态信息", required = true) @RequestBody @Valid Post post) {
        log.info("创建动态: {}", post);
        Post createdPost = postService.createPost(post);
        
        Set<Long> tagIds = post.getTagIds();
        if (!CollectionUtils.isEmpty(tagIds)) {
            tagIds.forEach(tagId -> postService.addTag(createdPost.getId(), tagId));
        }

        return Result.success(createdPost);
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
    public Result<Post> getPost(
            @ApiParam(value = "动态ID", required = true) 
            @PathVariable @NotNull(message = "动态ID不能为空") Long postId) {
        log.info("获取动态详情, postId: {}", postId);
        return Result.success(postService.getPost(postId));
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
    public Result<Page<Post>> getUserPosts(
            @ApiParam(value = "用户ID", required = true) 
            @PathVariable @NotNull(message = "用户ID不能为空") Long userId,
            @ApiParam(value = "页码，从1开始", defaultValue = "1") 
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码最小为1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") 
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页记录数最小为1") int size) {
        log.info("获取用户动态列表, userId: {}, page: {}, size: {}", userId, page, size);
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(postService.getUserPosts(userId, pageRequest));
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
            @ApiParam(value = "动态ID", required = true) 
            @PathVariable @NotNull(message = "动态ID不能为空") Long postId) {
        log.info("点赞动态, postId: {}", postId);
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
            @ApiParam(value = "动态ID", required = true) 
            @PathVariable @NotNull(message = "动态ID不能为空") Long postId) {
        log.info("取消点赞, postId: {}", postId);
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
    public Result<PostComment> commentPost(
            @ApiParam(value = "动态ID", required = true) 
            @PathVariable @NotNull(message = "动态ID不能为空") Long postId,
            @ApiParam(value = "评论信息", required = true) @RequestBody @Valid CommentRequest request) {
        log.info("评论动态, postId: {}, request: {}", postId, request);
        if (!StringUtils.hasText(request.getContent())) {
            return Result.error("评论内容不能为空");
        }
        return Result.success(postService.commentPost(
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
            @ApiParam(value = "动态ID", required = true) 
            @PathVariable @NotNull(message = "动态ID不能为空") Long postId,
            @ApiParam(value = "页码，从1开始", defaultValue = "1") 
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码最小为1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") 
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页记录数最小为1") int size) {
        log.info("获取动态评论树状结构, postId: {}, page: {}, size: {}", postId, page, size);
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
            @ApiParam(value = "动态ID", required = true) 
            @PathVariable @NotNull(message = "动态ID不能为空") Long postId,
            @ApiParam(value = "页码，从1开始", defaultValue = "1") 
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码最小为1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") 
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页记录数最小为1") int size) {
        log.info("获取动态评论扁平结构, postId: {}, page: {}, size: {}", postId, page, size);
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
            @ApiParam(value = "动态ID", required = true) 
            @PathVariable @NotNull(message = "动态ID不能为空") Long postId) {
        log.info("转发动态, postId: {}", postId);
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
            @ApiParam(value = "动态ID", required = true) 
            @PathVariable @NotNull(message = "动态ID不能为空") Long postId) {
        log.info("删除动态, postId: {}", postId);
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
    public Result<Post> updatePost(
            @ApiParam(value = "动态ID", required = true) 
            @PathVariable @NotNull(message = "动态ID不能为空") Long postId,
            @ApiParam(value = "更新的动态信息", required = true) @RequestBody @Valid Post post) {
        log.info("更新动态, postId: {}, post: {}", postId, post);
        if (post.getId() != null && !Objects.equals(postId, post.getId())) {
            return Result.error("路径中的动态ID与请求体中的动态ID不一致");
        }
        return Result.success(postService.updatePost(postId, post));
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
            @ApiParam(value = "动态ID", required = true) 
            @PathVariable @NotNull(message = "动态ID不能为空") Long postId) {
        log.info("检查是否点赞, postId: {}", postId);
        User currentUser = userService.getCurrentUser();
        return Result.success(postService.isLiked(postId, currentUser.getId()));
    }

    /**
     * 获取所有动态列表
     */
    @ApiOperation(value = "获取所有动态列表", notes = "获取系统中的所有动态，支持按标题和用户名筛选")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping
    public Result<Page<PostDTO>> getAllPosts(
            @ApiParam(value = "动态标题，用于筛选") @RequestParam(required = false) String title,
            @ApiParam(value = "用户名，用于筛选") @RequestParam(required = false) String userName,
            @ApiParam(value = "页码，从1开始", defaultValue = "1") 
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码最小为1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") 
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页记录数最小为1") int size) {
        log.info("获取所有动态列表, title: {}, userName: {}, page: {}, size: {}", title, userName, page, size);
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(postService.getAllPosts(title, userName, pageRequest));
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
    @PreAuthorize("hasRole('USER')")
    public Result<Page<PostDTO>> getFollowingPosts(
            @ApiParam(value = "页码，从1开始", defaultValue = "1") 
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码最小为1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") 
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页记录数最小为1") int size) {
        log.info("获取关注用户的动态列表, page: {}, size: {}", page, size);
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(postService.getFollowingPostsWithUserInfo(pageRequest));
    }

    /**
     * 管理员以指定用户身份发布动态
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
    public Result<Post> createPostByAdmin(
            @ApiParam(value = "用户ID", required = true) 
            @PathVariable @NotNull(message = "用户ID不能为空") Long userId,
            @ApiParam(value = "动态内容", required = true) @RequestBody @Valid Post post) {
        log.info("管理员以用户身份发布动态, userId: {}, post: {}", userId, post);
        
        // 验证用户ID
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        post.setUserId(userId); // 确保设置了正确的用户ID
        Post createdPost = postService.createPost(post);
        
        Set<Long> tagIds = post.getTagIds();
        if (!CollectionUtils.isEmpty(tagIds)) {
            tagIds.forEach(tagId -> postService.addTag(createdPost.getId(), tagId));
        }

        return Result.success(createdPost);
    }

    /**
     * 管理员删除动态
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
            @ApiParam(value = "动态ID", required = true) 
            @PathVariable @NotNull(message = "动态ID不能为空") Long postId) {
        log.info("管理员删除动态, postId: {}", postId);
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
    public Result<Page<Post>> getUserLikedPosts(
            @ApiParam(value = "页码，从1开始", defaultValue = "1") 
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码最小为1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10") 
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页记录数最小为1") int size) {
        log.info("获取用户点赞的动态列表, page: {}, size: {}", page, size);
        User currentUser = userService.getCurrentUser();
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(postService.getUserLikedPosts(currentUser.getId(), pageRequest));
    }

    /**
     * 管理员以指定用户身份评论动态
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
    public Result<PostComment> commentPostByAdmin(
            @ApiParam(value = "动态ID", required = true) 
            @PathVariable @NotNull(message = "动态ID不能为空") Long postId,
            @ApiParam(value = "用户ID", required = true) 
            @PathVariable @NotNull(message = "用户ID不能为空") Long userId,
            @ApiParam(value = "评论请求", required = true) @RequestBody @Valid CommentRequest request) {
        log.info("管理员以用户身份评论动态, postId: {}, userId: {}, request: {}", postId, userId, request);
        
        // 验证用户ID
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 验证评论内容
        if (!StringUtils.hasText(request.getContent())) {
            return Result.error("评论内容不能为空");
        }
        
        return Result.success(postService.commentPostByAdmin(
            postId,
            request.getContent(),
            request.getParentId(),
            userId
        ));
    }

    /**
     * 管理员删除评论
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
            @ApiParam(value = "评论ID", required = true) 
            @PathVariable @NotNull(message = "评论ID不能为空") Long commentId) {
        log.info("管理员删除评论, commentId: {}", commentId);
        postService.deleteCommentByAdmin(commentId);
        return Result.success(null);
    }

    /**
     * 点赞评论
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
            @ApiParam(value = "评论ID", required = true) 
            @PathVariable @NotNull(message = "评论ID不能为空") Long commentId) {
        log.info("点赞评论, commentId: {}", commentId);
        return Result.success(postService.likeComment(commentId));
    }

    /**
     * 取消评论点赞
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
            @ApiParam(value = "评论ID", required = true) 
            @PathVariable @NotNull(message = "评论ID不能为空") Long commentId) {
        log.info("取消评论点赞, commentId: {}", commentId);
        return Result.success(postService.unlikeComment(commentId));
    }

    /**
     * 检查用户是否已点赞评论
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
            @ApiParam(value = "评论ID", required = true) 
            @PathVariable @NotNull(message = "评论ID不能为空") Long commentId) {
        log.info("检查评论是否点赞, commentId: {}", commentId);
        User currentUser = userService.getCurrentUser();
        return Result.success(postService.isCommentLiked(commentId, currentUser.getId()));
    }

    /**
     * 管理员以指定用户身份点赞评论
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
            @ApiParam(value = "评论ID", required = true) 
            @PathVariable @NotNull(message = "评论ID不能为空") Long commentId,
            @ApiParam(value = "用户ID", required = true) 
            @PathVariable @NotNull(message = "用户ID不能为空") Long userId) {
        log.info("管理员以用户身份点赞评论, commentId: {}, userId: {}", commentId, userId);
        
        // 验证用户ID
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        return Result.success(postService.likeCommentByAdmin(commentId, userId));
    }

    /**
     * 删除评论
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
            @ApiParam(value = "评论ID", required = true) 
            @PathVariable @NotNull(message = "评论ID不能为空") Long commentId) {
        log.info("删除评论, commentId: {}", commentId);
        postService.deleteComment(commentId);
        return Result.success(null);
    }
}
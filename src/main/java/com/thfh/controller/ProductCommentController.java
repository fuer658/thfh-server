package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.ProductCommentDTO;
import com.thfh.model.User;
import com.thfh.service.ProductCommentService;
import com.thfh.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "商品评论管理", description = "提供商品评论的发布、查询、点赞、删除等功能")
@RestController
@RequestMapping("/api/products")
public class ProductCommentController {
    @Autowired
    private ProductCommentService productCommentService;
    @Autowired
    private UserService userService;

    /**
     * 商品评论
     */
    @ApiOperation(value = "评论商品", notes = "用户对商品发表评论，支持回复其他评论")
    @ApiResponses({
        @ApiResponse(code = 200, message = "评论成功", response = Result.class),
        @ApiResponse(code = 400, message = "请求参数错误", response = Result.class),
        @ApiResponse(code = 401, message = "未授权，请先登录", response = Result.class),
        @ApiResponse(code = 404, message = "商品不存在或父评论不存在", response = Result.class)
    })
    @PostMapping("/{productId}/comments")
    @PreAuthorize("hasRole('USER')")
    public Result<ProductCommentDTO> commentProduct(
            @ApiParam(value = "商品ID", required = true, example = "1") @PathVariable Long productId,
            @ApiParam(value = "评论信息", required = true) @Valid @RequestBody ProductCommentDTO request) {
        // 二级评论校验逻辑
        Long parentId = request.getParentId();
        if (parentId != null) {
            ProductCommentDTO parentComment = productCommentService.findById(parentId);
            if (parentComment == null) {
                return Result.error("父评论不存在");
            }
            if (parentComment.getParentId() != null) {
                return Result.error("只允许二级评论，不能回复二级评论");
            }
        }
        return Result.success(productCommentService.commentProductDTO(productId, request.getContent(), request.getParentId(), request.getImages()));
    }

    /**
     * 获取商品评论列表（树状结构）
     */
    @ApiOperation(value = "获取商品评论列表（树状结构）", notes = "获取指定商品的评论列表，以树状结构返回评论和回复")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功", response = Result.class),
        @ApiResponse(code = 401, message = "未授权，请先登录", response = Result.class),
        @ApiResponse(code = 404, message = "商品不存在", response = Result.class)
    })
    @GetMapping("/{productId}/comments/tree")
    public Result<Page<ProductCommentDTO>> getProductCommentTree(
            @ApiParam(value = "商品ID", required = true, example = "1") @PathVariable Long productId,
            @ApiParam(value = "页码", defaultValue = "1", example = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10", example = "10") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(productCommentService.getProductCommentTree(productId, pageRequest));
    }

    /**
     * 获取商品评论列表（扁平结构）
     */
    @ApiOperation(value = "获取商品评论列表（扁平结构）", notes = "获取指定商品的评论列表，以扁平结构返回所有评论")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功", response = Result.class),
        @ApiResponse(code = 401, message = "未授权，请先登录", response = Result.class),
        @ApiResponse(code = 404, message = "商品不存在", response = Result.class)
    })
    @GetMapping("/{productId}/comments")
    public Result<Page<ProductCommentDTO>> getProductComments(
            @ApiParam(value = "商品ID", required = true, example = "1") @PathVariable Long productId,
            @ApiParam(value = "页码", defaultValue = "1", example = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10", example = "10") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(productCommentService.getProductComments(productId, pageRequest));
    }

    /**
     * 点赞商品评论
     */
    @ApiOperation(value = "点赞商品评论", notes = "用户对商品评论进行点赞")
    @ApiResponses({
        @ApiResponse(code = 200, message = "点赞成功", response = Result.class),
        @ApiResponse(code = 401, message = "未授权，请先登录", response = Result.class),
        @ApiResponse(code = 404, message = "评论不存在", response = Result.class),
        @ApiResponse(code = 409, message = "已经点赞过该评论", response = Result.class)
    })
    @PostMapping("/comments/{commentId}/like")
    @PreAuthorize("hasRole('USER')")
    public Result<Integer> likeProductComment(
            @ApiParam(value = "评论ID", required = true, example = "1") @PathVariable Long commentId) {
        return Result.success(productCommentService.likeProductComment(commentId));
    }

    /**
     * 取消点赞商品评论
     */
    @ApiOperation(value = "取消点赞商品评论", notes = "用户取消对商品评论的点赞")
    @ApiResponses({
        @ApiResponse(code = 200, message = "取消成功", response = Result.class),
        @ApiResponse(code = 401, message = "未授权，请先登录", response = Result.class),
        @ApiResponse(code = 404, message = "评论不存在或未点赞", response = Result.class)
    })
    @DeleteMapping("/comments/{commentId}/like")
    @PreAuthorize("hasRole('USER')")
    public Result<Integer> unlikeProductComment(
            @ApiParam(value = "评论ID", required = true, example = "1") @PathVariable Long commentId) {
        return Result.success(productCommentService.unlikeProductComment(commentId));
    }

    /**
     * 检查用户是否已点赞商品评论
     */
    @ApiOperation(value = "检查用户是否已点赞商品评论", notes = "检查当前登录用户是否已经对指定商品评论点赞")
    @ApiResponses({
        @ApiResponse(code = 200, message = "检查成功", response = Result.class),
        @ApiResponse(code = 401, message = "未授权，请先登录", response = Result.class),
        @ApiResponse(code = 404, message = "评论不存在", response = Result.class)
    })
    @GetMapping("/comments/{commentId}/isLiked")
    @PreAuthorize("hasRole('USER')")
    public Result<Boolean> isProductCommentLiked(
            @ApiParam(value = "评论ID", required = true, example = "1") @PathVariable Long commentId) {
        User currentUser = userService.getCurrentUser();
        return Result.success(productCommentService.isProductCommentLiked(commentId, currentUser.getId()));
    }

    /**
     * 删除商品评论（用户）
     */
    @ApiOperation(value = "删除商品评论", notes = "用户删除自己发布的商品评论")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功", response = Result.class),
        @ApiResponse(code = 401, message = "未授权，请先登录", response = Result.class),
        @ApiResponse(code = 403, message = "没有权限删除该评论", response = Result.class),
        @ApiResponse(code = 404, message = "评论不存在", response = Result.class)
    })
    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> deleteProductComment(
            @ApiParam(value = "评论ID", required = true, example = "1") @PathVariable Long commentId) {
        productCommentService.deleteProductComment(commentId);
        return Result.success(null);
    }

    /**
     * 删除商品评论（管理员）
     */
    @ApiOperation(value = "管理员删除商品评论", notes = "管理员可以删除任何用户的商品评论，仅管理员可操作")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功", response = Result.class),
        @ApiResponse(code = 401, message = "未授权，请先登录", response = Result.class),
        @ApiResponse(code = 403, message = "没有管理员权限", response = Result.class),
        @ApiResponse(code = 404, message = "评论不存在", response = Result.class)
    })
    @DeleteMapping("/admin/comments/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteProductCommentByAdmin(
            @ApiParam(value = "评论ID", required = true, example = "1") @PathVariable Long commentId) {
        productCommentService.deleteProductCommentByAdmin(commentId);
        return Result.success(null);
    }

    /**
     * 查看自己发布的商品评论
     */
    @ApiOperation(value = "查看自己发布的商品评论", notes = "获取当前登录用户发布的所有商品评论，支持分页")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功", response = Result.class),
        @ApiResponse(code = 401, message = "未授权，请先登录", response = Result.class)
    })
    @GetMapping("/comments/my")
    @PreAuthorize("hasRole('USER')")
    public Result<Page<ProductCommentDTO>> getMyProductComments(
            @ApiParam(value = "页码", defaultValue = "1", example = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页记录数", defaultValue = "10", example = "10") @RequestParam(defaultValue = "10") int size) {
        User currentUser = userService.getCurrentUser();
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(productCommentService.getCommentsByUser(currentUser.getId(), pageRequest));
    }

    /**
     * 追加商品评论（追评）
     */
    @ApiOperation(value = "追加商品评论（追评）", notes = "用户对自己已发布的商品评论进行追加评论，每条评论仅允许追评一次")
    @ApiResponses({
        @ApiResponse(code = 200, message = "追评成功", response = Result.class),
        @ApiResponse(code = 400, message = "请求参数错误", response = Result.class),
        @ApiResponse(code = 401, message = "未授权，请先登录", response = Result.class),
        @ApiResponse(code = 403, message = "没有权限追加评论", response = Result.class),
        @ApiResponse(code = 404, message = "评论不存在", response = Result.class),
        @ApiResponse(code = 409, message = "该评论已追评", response = Result.class)
    })
    @PostMapping("/comments/{commentId}/append")
    @PreAuthorize("hasRole('USER')")
    public Result<ProductCommentDTO> appendProductComment(
            @ApiParam(value = "主评论ID", required = true, example = "1") @PathVariable Long commentId,
            @ApiParam(value = "追评内容", required = true) @Valid @RequestBody ProductCommentDTO request) {
        User currentUser = userService.getCurrentUser();
        ProductCommentDTO mainComment = productCommentService.findById(commentId);
        if (mainComment == null) {
            return Result.error("评论不存在");
        }
        if (!mainComment.getUserId().equals(currentUser.getId())) {
            return Result.error("没有权限追加评论");
        }
        if (mainComment.getAppendComment() != null) {
            return Result.error("该评论已追评");
        }
        if (mainComment.getParentId() != null) {
            return Result.error("只允许对一级评论追评");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return Result.error("追评内容不能为空");
        }
        return Result.success(productCommentService.appendProductComment(commentId, request.getContent(), request.getImages()));
    }
} 
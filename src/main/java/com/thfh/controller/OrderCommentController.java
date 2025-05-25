package com.thfh.controller;

import com.thfh.dto.OrderCommentDTO;
import com.thfh.dto.OrderCommentCreateDTO;
import com.thfh.model.OrderComment;
import com.thfh.service.OrderCommentService;
import com.thfh.service.OrderCommentFileService;
import com.thfh.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单评价控制器
 */
@Tag(name = "订单评价", description = "提供订单评价的创建、查询等功能")
@RestController
@RequestMapping("/api/order-comments")
public class OrderCommentController {

    @Autowired
    private OrderCommentService orderCommentService;

    @Autowired
    private OrderCommentFileService orderCommentFileService;

    /**
     * 创建订单评价 (使用JSON请求体)
     * @param orderId 订单ID
     * @param commentDTO 评价数据
     * @return 创建的评价
     */
    @Operation(summary = "创建订单评价(JSON)", description = "对已完成的订单进行评价，使用JSON请求体")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "评价成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "无权评价该订单"),
        @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    @PostMapping(value = "/{orderId}/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<OrderCommentDTO> createCommentWithJson(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId,
            @Parameter(description = "评价数据", required = true) @RequestBody OrderCommentCreateDTO commentDTO) {
        
        try {
            OrderComment comment = orderCommentService.createComment(
                orderId, 
                commentDTO.getContent(), 
                commentDTO.getImageUrls(), 
                commentDTO.getVideoUrl(), 
                commentDTO.getScore()
            );
            return Result.success(orderCommentService.toOrderCommentDTO(comment), "评价成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建订单评价
     * @param orderId 订单ID
     * @param content 评价内容
     * @param score 评分
     * @param images 评价图片
     * @param video 评价视频
     * @return 创建的评价
     */
    @Operation(summary = "创建订单评价", description = "对已完成的订单进行评价")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "评价成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "无权评价该订单"),
        @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    @PostMapping(value = "/{orderId}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<OrderCommentDTO> createComment(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId,
            @Parameter(description = "评价内容", required = true) @RequestParam String content,
            @Parameter(description = "评分（1-10分）", required = true) @RequestParam Integer score,
            @Parameter(description = "评价图片") @RequestParam(required = false) List<MultipartFile> images,
            @Parameter(description = "评价视频") @RequestParam(required = false) MultipartFile video) {
        
        try {
            // 处理图片上传
            List<String> imageUrls = new ArrayList<>();
            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    String imageUrl = orderCommentFileService.uploadImage(image);
                    imageUrls.add(imageUrl);
                }
            }

            // 处理视频上传
            String videoUrl = null;
            if (video != null && !video.isEmpty()) {
                videoUrl = orderCommentFileService.uploadVideo(video);
            }

            OrderComment comment = orderCommentService.createComment(orderId, content, imageUrls, videoUrl, score);
            return Result.success(orderCommentService.toOrderCommentDTO(comment), "评价成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取订单的评价列表
     * @param orderId 订单ID
     * @return 评价列表
     */
    @Operation(summary = "获取订单评价列表", description = "获取指定订单的所有评价")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    @GetMapping("/order/{orderId}")
    public Result<List<OrderCommentDTO>> getCommentsByOrderId(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId) {
        List<OrderCommentDTO> comments = orderCommentService.getCommentsByOrderId(orderId);
        return Result.success(comments);
    }

    /**
     * 获取艺术品的评价分页
     * @param artworkId 艺术品ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 评价分页
     */
    @Operation(summary = "获取艺术品评价", description = "获取指定艺术品的评价分页列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "艺术品不存在")
    })
    @GetMapping("/artwork/{artworkId}")
    public Result<Page<OrderCommentDTO>> getCommentsByArtworkId(
            @Parameter(description = "艺术品ID", required = true) @PathVariable Long artworkId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        Page<OrderCommentDTO> page = orderCommentService.getCommentsByArtworkId(artworkId, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 获取当前用户的评价分页
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 评价分页
     */
    @Operation(summary = "获取我的评价", description = "获取当前登录用户的评价分页列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Page<OrderCommentDTO>> getCurrentUserComments(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        Page<OrderCommentDTO> page = orderCommentService.getCurrentUserComments(pageNum, pageSize);
        return Result.success(page);
    }
    
    /**
     * 点赞评论
     * @param commentId 评论ID
     * @return 更新后的点赞数
     */
    @Operation(summary = "点赞评论", description = "对评论进行点赞")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "点赞成功"),
        @ApiResponse(responseCode = "400", description = "已点赞过该评论"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "评论不存在")
    })
    @PostMapping("/{commentId}/like")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Integer> likeComment(
            @Parameter(description = "评论ID", required = true) @PathVariable Long commentId) {
        try {
            int likeCount = orderCommentService.likeComment(commentId);
            return Result.success(likeCount, "点赞成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 取消点赞评论
     * @param commentId 评论ID
     * @return 更新后的点赞数
     */
    @Operation(summary = "取消点赞评论", description = "取消对评论的点赞")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "取消点赞成功"),
        @ApiResponse(responseCode = "400", description = "未点赞过该评论"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "评论不存在")
    })
    @DeleteMapping("/{commentId}/like")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Integer> unlikeComment(
            @Parameter(description = "评论ID", required = true) @PathVariable Long commentId) {
        try {
            int likeCount = orderCommentService.unlikeComment(commentId);
            return Result.success(likeCount, "取消点赞成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 检查是否已点赞评论
     * @param commentId 评论ID
     * @return 是否已点赞
     */
    @Operation(summary = "检查是否已点赞", description = "检查当前用户是否已点赞评论")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "评论不存在")
    })
    @GetMapping("/{commentId}/liked")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Boolean> isCommentLiked(
            @Parameter(description = "评论ID", required = true) @PathVariable Long commentId) {
        boolean liked = orderCommentService.isCommentLiked(commentId);
        return Result.success(liked);
    }
} 
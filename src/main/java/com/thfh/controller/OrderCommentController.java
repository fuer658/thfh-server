package com.thfh.controller;

import com.thfh.dto.OrderCommentDTO;
import com.thfh.model.OrderComment;
import com.thfh.service.OrderCommentService;
import com.thfh.service.OrderCommentFileService;
import com.thfh.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单评价控制器
 */
@Api(tags = "订单评价", description = "提供订单评价的创建、查询等功能")
@RestController
@RequestMapping("/api/order-comments")
public class OrderCommentController {

    @Autowired
    private OrderCommentService orderCommentService;

    @Autowired
    private OrderCommentFileService orderCommentFileService;

    /**
     * 创建订单评价
     * @param orderId 订单ID
     * @param content 评价内容
     * @param score 评分
     * @param images 评价图片
     * @param video 评价视频
     * @return 创建的评价
     */
    @ApiOperation(value = "创建订单评价", notes = "对已完成的订单进行评价")
    @ApiResponses({
        @ApiResponse(code = 200, message = "评价成功"),
        @ApiResponse(code = 400, message = "参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "无权评价该订单"),
        @ApiResponse(code = 404, message = "订单不存在")
    })
    @PostMapping(value = "/{orderId}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<OrderCommentDTO> createComment(
            @ApiParam(value = "订单ID", required = true) @PathVariable Long orderId,
            @ApiParam(value = "评价内容", required = true) @RequestParam String content,
            @ApiParam(value = "评分（1-10分）", required = true) @RequestParam Integer score,
            @ApiParam(value = "评价图片") @RequestParam(required = false) List<MultipartFile> images,
            @ApiParam(value = "评价视频") @RequestParam(required = false) MultipartFile video) {
        
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
    @ApiOperation(value = "获取订单评价列表", notes = "获取指定订单的所有评价")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "订单不存在")
    })
    @GetMapping("/order/{orderId}")
    public Result<List<OrderCommentDTO>> getCommentsByOrderId(
            @ApiParam(value = "订单ID", required = true) @PathVariable Long orderId) {
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
    @ApiOperation(value = "获取艺术品评价", notes = "获取指定艺术品的评价分页列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "艺术品不存在")
    })
    @GetMapping("/artwork/{artworkId}")
    public Result<Page<OrderCommentDTO>> getCommentsByArtworkId(
            @ApiParam(value = "艺术品ID", required = true) @PathVariable Long artworkId,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") int pageSize) {
        Page<OrderCommentDTO> page = orderCommentService.getCommentsByArtworkId(artworkId, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 获取当前用户的评价分页
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 评价分页
     */
    @ApiOperation(value = "获取我的评价", notes = "获取当前登录用户的评价分页列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Page<OrderCommentDTO>> getCurrentUserComments(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") int pageSize) {
        Page<OrderCommentDTO> page = orderCommentService.getCurrentUserComments(pageNum, pageSize);
        return Result.success(page);
    }
} 
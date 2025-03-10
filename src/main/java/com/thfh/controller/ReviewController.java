package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.ReviewDTO;
import com.thfh.dto.ReviewQueryDTO;
import com.thfh.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 评论管理控制器
 * 提供评论的查询、删除和状态切换等功能
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    /**
     * 获取评论列表
     * @param queryDTO 查询条件，包含作品ID、用户ID和分页信息等
     * @return 评论分页列表
     */
    @GetMapping
    public Result<Page<ReviewDTO>> getReviews(ReviewQueryDTO queryDTO) {
        return Result.success(reviewService.getReviews(queryDTO));
    }

    /**
     * 删除评论
     * @param id 评论ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return Result.success(null);
    }

    /**
     * 切换评论状态（显示/隐藏）
     * @param id 评论ID
     * @return 操作结果
     */
    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleReviewStatus(@PathVariable Long id) {
        reviewService.toggleReviewStatus(id);
        return Result.success(null);
    }
} 
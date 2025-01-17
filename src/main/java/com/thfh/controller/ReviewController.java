package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.ReviewDTO;
import com.thfh.dto.ReviewQueryDTO;
import com.thfh.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public Result<Page<ReviewDTO>> getReviews(ReviewQueryDTO queryDTO) {
        return Result.success(reviewService.getReviews(queryDTO));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return Result.success(null);
    }

    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleReviewStatus(@PathVariable Long id) {
        reviewService.toggleReviewStatus(id);
        return Result.success(null);
    }
} 
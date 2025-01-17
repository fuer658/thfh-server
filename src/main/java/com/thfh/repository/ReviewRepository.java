package com.thfh.repository;

import com.thfh.model.Review;
import com.thfh.model.ReviewType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    long countByTypeAndTargetIdAndRating(ReviewType type, Long targetId, Integer rating);
} 
package com.thfh.repository;

import com.thfh.model.ProductCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductCommentLikeRepository extends JpaRepository<ProductCommentLike, Long> {
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);
    Optional<ProductCommentLike> findByCommentIdAndUserId(Long commentId, Long userId);
    void deleteByCommentIdAndUserId(Long commentId, Long userId);
    int countByCommentId(Long commentId);
} 
package com.thfh.repository;

import com.thfh.model.ProductComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProductCommentRepository extends JpaRepository<ProductComment, Long> {
    // 可根据需要添加自定义查询方法

    /**
     * 分页查询商品的一级评论（parentId为null）
     */
    Page<ProductComment> findByProductIdAndParentIdIsNull(Long productId, Pageable pageable);

    /**
     * 批量查询二级评论（parentId in ...）
     */
    java.util.List<ProductComment> findByParentIdIn(java.util.List<Long> parentIds);

    /**
     * 分页查询用户的所有评论
     */
    Page<ProductComment> findByUserId(Long userId, Pageable pageable);
} 
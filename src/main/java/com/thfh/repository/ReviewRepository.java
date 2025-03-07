package com.thfh.repository;

import com.thfh.model.Review;
import com.thfh.model.ReviewType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 评价数据访问接口
 * 提供对评价(Review)实体的数据库操作功能
 * 继承自JpaRepository，自动提供基本的CRUD操作
 * 实现JpaSpecificationExecutor接口，支持复杂条件查询
 */
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    
    /**
     * 统计指定类型、目标ID和评分的评价数量
     * 
     * @param type 评价类型
     * @param targetId 目标ID
     * @param rating 评分
     * @return 符合条件的评价数量
     */
    long countByTypeAndTargetIdAndRating(ReviewType type, Long targetId, Integer rating);
} 
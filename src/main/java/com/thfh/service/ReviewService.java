package com.thfh.service;

import com.thfh.dto.ReviewDTO;
import com.thfh.dto.ReviewQueryDTO;
import com.thfh.model.Review;
import com.thfh.model.Course;
import com.thfh.model.ReviewType;
import com.thfh.model.Artwork;
import com.thfh.repository.ReviewRepository;
import com.thfh.repository.CourseRepository;
import com.thfh.repository.ArtworkRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 评价服务类
 * 提供评价相关的业务逻辑处理，包括评价的查询、删除等操作
 * 以及评价状态管理等功能
 */
@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

    /**
     * 根据查询条件获取评价列表
     * @param queryDTO 查询条件对象，包含评价类型、目标ID、用户ID、评分、启用状态等过滤条件
     * @return 分页后的评价DTO列表
     */
    public Page<ReviewDTO> getReviews(ReviewQueryDTO queryDTO) {
        Specification<Review> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (queryDTO.getType() != null) {
                predicates.add(cb.equal(root.get("type"), queryDTO.getType()));
            }
            if (queryDTO.getTargetId() != null) {
                predicates.add(cb.equal(root.get("targetId"), queryDTO.getTargetId()));
            }
            if (queryDTO.getUserId() != null) {
                predicates.add(cb.equal(root.get("user").get("id"), queryDTO.getUserId()));
            }
            if (queryDTO.getRating() != null) {
                predicates.add(cb.equal(root.get("rating"), queryDTO.getRating()));
            }
            if (queryDTO.getEnabled() != null) {
                predicates.add(cb.equal(root.get("enabled"), queryDTO.getEnabled()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Page<Review> reviewPage = reviewRepository.findAll(spec, 
            PageRequest.of(queryDTO.getPageNum() - 1, queryDTO.getPageSize(), sort));
        
        return reviewPage.map(this::convertToDTO);
    }

    /**
     * 删除指定ID的评价
     * @param id 要删除的评价ID
     */
    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    /**
     * 切换评价启用状态
     * 如果评价当前是启用状态，则禁用；如果是禁用状态，则启用
     * @param id 评价ID
     * @throws RuntimeException 当评价不存在时抛出
     */
    @Transactional
    public void toggleReviewStatus(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("评价不存在"));
        review.setEnabled(!review.getEnabled());
        reviewRepository.save(review);
    }

    /**
     * 将评价实体对象转换为DTO对象
     * 同时获取评价目标的名称（课程或作品）
     * @param review 评价实体对象
     * @return 转换后的评价DTO对象
     */
    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        BeanUtils.copyProperties(review, dto);
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getRealName());

        // 获取评价目标名称
        if (review.getType() == ReviewType.COURSE) {
            Course course = courseRepository.findById(review.getTargetId())
                    .orElse(null);
            if (course != null) {
                dto.setTargetName(course.getTitle());
            }
        } else if (review.getType() == ReviewType.WORK) {
            Artwork artwork = artworkRepository.findById(review.getTargetId())
                    .orElse(null);
            if (artwork != null) {
                dto.setTargetName(artwork.getTitle());
            }
        }

        return dto;
    }
}
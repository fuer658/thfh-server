package com.thfh.service;

import com.thfh.dto.ReviewDTO;
import com.thfh.dto.ReviewQueryDTO;
import com.thfh.model.Review;
import com.thfh.model.Course;
import com.thfh.model.ReviewType;
import com.thfh.model.Work;
import com.thfh.repository.ReviewRepository;
import com.thfh.repository.CourseRepository;
import com.thfh.repository.WorkRepository;
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

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private WorkRepository workRepository;

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

    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    @Transactional
    public void toggleReviewStatus(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("评价不存在"));
        review.setEnabled(!review.getEnabled());
        reviewRepository.save(review);
    }

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
            Work work = workRepository.findById(review.getTargetId())
                    .orElse(null);
            if (work != null) {
                dto.setTargetName(work.getTitle());
            }
        }

        return dto;
    }
} 
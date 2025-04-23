package com.thfh.repository;

import com.thfh.model.CourseDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CourseDetailRepository extends JpaRepository<CourseDetail, Long> {
    
    /**
     * 根据课程ID查找课程详情
     * @param courseId 课程ID
     * @return 课程详情对象
     */
    Optional<CourseDetail> findByCourseId(Long courseId);
    
    /**
     * 检查指定课程ID的课程详情是否存在
     * @param courseId 课程ID
     * @return 是否存在
     */
    boolean existsByCourseId(Long courseId);
} 
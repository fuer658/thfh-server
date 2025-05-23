package com.thfh.repository;

import com.thfh.model.CoursePointsPurchase;
import com.thfh.model.User;
import com.thfh.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 课程积分购买记录仓库接口
 */
@Repository
public interface CoursePointsPurchaseRepository extends JpaRepository<CoursePointsPurchase, Long> {
    
    /**
     * 查询用户所有的课程积分购买记录
     * @param user 用户
     * @return 购买记录列表
     */
    List<CoursePointsPurchase> findByUser(User user);
    
    /**
     * 分页查询用户的课程积分购买记录
     * @param user 用户
     * @param pageable 分页参数
     * @return 分页购买记录
     */
    Page<CoursePointsPurchase> findByUser(User user, Pageable pageable);
    
    /**
     * 查询用户的特定课程购买记录
     * @param user 用户
     * @param course 课程
     * @return 可选的购买记录
     */
    Optional<CoursePointsPurchase> findByUserAndCourse(User user, Course course);
    
    /**
     * 查询用户在指定时间范围内的购买记录
     * @param user 用户
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 购买记录列表
     */
    List<CoursePointsPurchase> findByUserAndCreateTimeBetween(User user, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查询特定课程的所有购买记录
     * @param course 课程
     * @return 购买记录列表
     */
    List<CoursePointsPurchase> findByCourse(Course course);
    
    /**
     * 统计用户购买的课程数量
     * @param user 用户
     * @param status 购买状态
     * @return 购买成功的课程数量
     */
    long countByUserAndStatus(User user, CoursePointsPurchase.PurchaseStatus status);
} 
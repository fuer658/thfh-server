package com.thfh.service;

import com.thfh.model.CoursePointsPurchase;
import com.thfh.model.User;
import com.thfh.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 课程积分购买服务接口
 */
public interface CoursePointsPurchaseService {

    /**
     * 使用积分购买课程
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 购买记录
     */
    CoursePointsPurchase purchaseCourseWithPoints(Long userId, Long courseId);
    
    /**
     * 查询用户的课程购买记录
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页购买记录
     */
    Page<CoursePointsPurchase> getUserPurchaseRecords(Long userId, Pageable pageable);
    
    /**
     * 查询用户是否已购买某课程
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 是否已购买
     */
    boolean hasPurchasedCourse(Long userId, Long courseId);
    
    /**
     * 获取购买记录详情
     * @param purchaseId 购买记录ID
     * @return 购买记录
     */
    Optional<CoursePointsPurchase> getPurchaseDetails(Long purchaseId);
    
    /**
     * 退款处理
     * @param purchaseId 购买记录ID
     * @return 更新后的购买记录
     */
    CoursePointsPurchase refundPurchase(Long purchaseId);
    
    /**
     * 获取课程的所有购买记录
     * @param courseId 课程ID
     * @return 购买记录列表
     */
    List<CoursePointsPurchase> getCoursePurchases(Long courseId);
} 
package com.thfh.service.impl;

import com.thfh.exception.ResourceNotFoundException;
import com.thfh.exception.BadRequestException;
import com.thfh.model.Course;
import com.thfh.model.CoursePointsPurchase;
import com.thfh.model.PointsRecord;
import com.thfh.model.PointsType;
import com.thfh.model.User;
import com.thfh.model.UserCourse;
import com.thfh.repository.CoursePointsPurchaseRepository;
import com.thfh.repository.CourseRepository;
import com.thfh.repository.PointsRecordRepository;
import com.thfh.repository.UserCourseRepository;
import com.thfh.repository.UserRepository;
import com.thfh.service.CoursePointsPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

/**
 * 课程积分购买服务实现类
 */
@Service
public class CoursePointsPurchaseServiceImpl implements CoursePointsPurchaseService {

    @Autowired
    private CoursePointsPurchaseRepository purchaseRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PointsRecordRepository pointsRecordRepository;
    
    @Autowired
    private UserCourseRepository userCourseRepository;

    /**
     * 使用积分购买课程
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 购买记录
     */
    @Override
    @Transactional
    public CoursePointsPurchase purchaseCourseWithPoints(Long userId, Long courseId) {
        // 获取用户和课程
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("课程不存在"));
        
        // 检查用户是否已购买该课程
        if (hasPurchasedCourse(userId, courseId)) {
            throw new BadRequestException("您已购买过该课程");
        }
        
        // 检查课程是否支持积分购买
        if (course.getPointsPrice() == null || course.getPointsPrice() <= 0) {
            throw new BadRequestException("该课程不支持积分购买");
        }
        
        // 判断用户是否为残疾学员（disability不为空）
        boolean isDisabledStudent = StringUtils.isNotBlank(user.getDisability());
        
        // 创建购买记录
        CoursePointsPurchase purchase = new CoursePointsPurchase();
        purchase.setUser(user);
        purchase.setCourse(course);
        purchase.setStatus(CoursePointsPurchase.PurchaseStatus.SUCCESS);
        
        int requiredPoints = course.getPointsPrice();
        
        if (isDisabledStudent) {
            // 残疾学员免费获取课程
            purchase.setPointsSpent(0);
            purchase.setRemark("残疾学员免费获取课程：" + course.getTitle());
        } else {
            // 检查用户积分是否足够
            if (user.getPoints() < requiredPoints) {
                throw new BadRequestException("积分不足，无法购买课程");
            }
            
            // 扣除用户积分
            user.setPoints(user.getPoints() - requiredPoints);
            userRepository.save(user);
            
            // 记录积分变动
            PointsRecord pointsRecord = new PointsRecord();
            pointsRecord.setStudent(user);
            pointsRecord.setPoints(-requiredPoints);
            pointsRecord.setType(PointsType.EXCHANGE_COURSE);
            pointsRecord.setDescription("购买课程：" + course.getTitle());
            pointsRecordRepository.save(pointsRecord);
            
            purchase.setPointsSpent(requiredPoints);
            purchase.setRemark("积分购买课程：" + course.getTitle());
        }
        
        purchase.setCreateTime(LocalDateTime.now());
        
        // 更新课程学生数量
        course.setStudentCount(course.getStudentCount() + 1);
        courseRepository.save(course);
        
        // 创建用户-课程关联
        UserCourse userCourse = new UserCourse();
        userCourse.setUser(user);
        userCourse.setCourse(course);
        userCourse.setEnrollTime(LocalDateTime.now());
        userCourse.setLastAccessTime(LocalDateTime.now());
        userCourse.setIsActive(true);
        userCourseRepository.save(userCourse);
        
        // 保存并返回购买记录
        return purchaseRepository.save(purchase);
    }

    /**
     * 查询用户的课程购买记录
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页购买记录
     */
    @Override
    public Page<CoursePointsPurchase> getUserPurchaseRecords(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        return purchaseRepository.findByUser(user, pageable);
    }

    /**
     * 查询用户是否已购买某课程
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 是否已购买
     */
    @Override
    public boolean hasPurchasedCourse(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("课程不存在"));
        
        Optional<CoursePointsPurchase> purchase = purchaseRepository.findByUserAndCourse(user, course);
        
        return purchase.isPresent() && 
               purchase.get().getStatus() == CoursePointsPurchase.PurchaseStatus.SUCCESS;
    }

    /**
     * 获取购买记录详情
     * @param purchaseId 购买记录ID
     * @return 购买记录
     */
    @Override
    public Optional<CoursePointsPurchase> getPurchaseDetails(Long purchaseId) {
        return purchaseRepository.findById(purchaseId);
    }

    /**
     * 退款处理
     * @param purchaseId 购买记录ID
     * @return 更新后的购买记录
     */
    @Override
    @Transactional
    public CoursePointsPurchase refundPurchase(Long purchaseId) {
        CoursePointsPurchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException("购买记录不存在"));
        
        // 检查是否已经退款
        if (purchase.getStatus() == CoursePointsPurchase.PurchaseStatus.REFUNDED) {
            throw new BadRequestException("该购买记录已经退款");
        }
        
        // 检查购买状态
        if (purchase.getStatus() != CoursePointsPurchase.PurchaseStatus.SUCCESS) {
            throw new BadRequestException("只有成功的购买才能退款");
        }
        
        User user = purchase.getUser();
        Course course = purchase.getCourse();
        int refundPoints = purchase.getPointsSpent();
        
        // 只有当消费的积分大于0时才进行退还操作 (处理残疾学员情况)
        if (refundPoints > 0) {
            // 退还积分
            user.setPoints(user.getPoints() + refundPoints);
            userRepository.save(user);
            
            // 记录积分变动
            PointsRecord pointsRecord = new PointsRecord();
            pointsRecord.setStudent(user);
            pointsRecord.setPoints(refundPoints);
            pointsRecord.setType(PointsType.ADMIN_ADJUST);
            pointsRecord.setDescription("退款：课程《" + course.getTitle() + "》积分退还");
            pointsRecordRepository.save(pointsRecord);
        }
        
        // 更新课程学生数量
        course.setStudentCount(course.getStudentCount() - 1);
        courseRepository.save(course);
        
        // 删除用户-课程关联
        userCourseRepository.findByUserAndCourse(user, course)
                .ifPresent(userCourse -> userCourseRepository.delete(userCourse));
        
        // 更新购买记录状态
        purchase.setStatus(CoursePointsPurchase.PurchaseStatus.REFUNDED);
        purchase.setUpdateTime(LocalDateTime.now());
        return purchaseRepository.save(purchase);
    }

    /**
     * 获取课程的所有购买记录
     * @param courseId 课程ID
     * @return 购买记录列表
     */
    @Override
    public List<CoursePointsPurchase> getCoursePurchases(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("课程不存在"));
        return purchaseRepository.findByCourse(course);
    }
} 
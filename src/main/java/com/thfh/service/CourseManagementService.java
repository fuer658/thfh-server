package com.thfh.service;

import com.thfh.dto.CourseDTO;
import com.thfh.dto.CourseInteractionDTO;
import com.thfh.dto.CourseQueryDTO;
import com.thfh.dto.SimpleUserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 课程管理服务接口
 * 提供课程相关的业务逻辑处理，包括课程的创建、查询、修改、删除等操作
 * 以及课程状态管理等功能
 */
public interface CourseManagementService {
    
    /**
     * 根据查询条件获取课程列表
     * @param queryDTO 查询条件对象，包含课程标题、教员ID、状态、启用状态等过滤条件
     * @return 分页后的课程DTO列表
     */
    Page<CourseDTO> getCourses(CourseQueryDTO queryDTO);
    
    /**
     * 创建新课程
     * @param courseDTO 课程信息对象，包含课程的基本信息
     * @return 创建成功的课程DTO对象
     */
    CourseDTO createCourse(CourseDTO courseDTO);
    
    /**
     * 更新课程信息
     * @param id 课程ID
     * @param courseDTO 更新后的课程信息对象
     * @return 更新后的课程DTO对象
     */
    CourseDTO updateCourse(Long id, CourseDTO courseDTO);
    
    /**
     * 删除指定ID的课程
     * @param id 要删除的课程ID
     */
    void deleteCourse(Long id);
    
    /**
     * 切换课程启用状态
     * 如果课程当前是启用状态，则禁用；如果是禁用状态，则启用
     * @param id 课程ID
     */
    void toggleCourseStatus(Long id);
    
    /**
     * 点赞/取消点赞课程
     * @param courseId 课程ID
     * @param userId 用户ID
     */
    void toggleCourseLike(Long courseId, Long userId);
    
    /**
     * 收藏/取消收藏课程
     * @param courseId 课程ID
     * @param userId 用户ID
     */
    void toggleCourseFavorite(Long courseId, Long userId);
    
    /**
     * 学生加入课程
     * @param courseId 课程ID
     * @param userId 学生ID
     * @return 加入后的课程信息
     */
    CourseDTO enrollCourse(Long courseId, Long userId);
    
    /**
     * 学生退出课程
     * @param courseId 课程ID
     * @param userId 学生ID
     */
    void unenrollCourse(Long courseId, Long userId);
    
    /**
     * 获取课程的学生列表
     * @param courseId 课程ID
     * @return 学生列表，包含基本信息（ID、姓名、头像）
     */
    List<SimpleUserDTO> getCourseStudents(Long courseId);
    
    /**
     * 获取用户对课程的交互信息（点赞和收藏状态）
     * @param courseId 课程ID
     * @param userId 用户ID
     * @return 包含点赞和收藏状态的对象
     */
    CourseInteractionDTO getCourseInteractionInfo(Long courseId, Long userId);
    
    /**
     * 获取课程的点赞和收藏用户列表
     * @param courseId 课程ID
     * @return 包含点赞和收藏用户列表的对象
     */
    Map<String, List<SimpleUserDTO>> getCourseInteractionUsers(Long courseId);
    
    /**
     * 获取用户收藏的课程列表
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页后的课程列表
     */
    Page<CourseDTO> getUserFavoriteCourses(Long userId, Pageable pageable);
    
    /**
     * 获取课程详情
     * @param id 课程ID
     * @return 课程详情
     */
    CourseDTO getCourseDetail(Long id);
    
    /**
     * 发布课程
     * @param id 课程ID
     * @return 发布后的课程信息
     */
    CourseDTO publishCourse(Long id);
} 
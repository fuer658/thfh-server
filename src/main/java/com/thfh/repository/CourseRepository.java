package com.thfh.repository;

import com.thfh.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 课程数据访问接口
 * 提供对课程(Course)实体的数据库操作功能
 * 继承自JpaRepository，自动提供基本的CRUD操作
 * 实现JpaSpecificationExecutor接口，支持复杂条件查询
 */
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    
    /**
     * 检查指定标题和教师ID的课程是否已存在
     * 
     * @param title 课程标题
     * @param teacherId 教师ID
     * @return 如果课程已存在返回true，否则返回false
     */
    boolean existsByTitleAndTeacherId(String title, Long teacherId);
} 
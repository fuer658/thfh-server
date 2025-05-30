package com.thfh.repository;

import com.thfh.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * 浏览量自增
     * @param id 课程ID
     */
    @Modifying
    @Query("update Course c set c.viewCount = c.viewCount + 1 where c.id = :id")
    int increaseViewCountById(@Param("id") Long id);

    /**
     * 分页查询热门课程，按指定字段降序排序
     * @param pageable 分页和排序参数
     * @return 课程分页列表
     */
    Page<Course> findAllByStatusAndEnabledTrue(com.thfh.model.CourseStatus status, Pageable pageable);
} 
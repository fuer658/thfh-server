package com.thfh.repository;

import com.thfh.model.UserCourse;
import com.thfh.model.User;
import com.thfh.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long>, JpaSpecificationExecutor<UserCourse> {
    /**
     * 查找指定课程的所有选课记录
     * @param course 课程实体
     * @return 选课记录列表
     */
    List<UserCourse> findByCourse(Course course);
    Optional<UserCourse> findByUserAndCourse(User user, Course course);
    boolean existsByUserAndCourse(User user, Course course);
}
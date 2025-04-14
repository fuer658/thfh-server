package com.thfh.repository;

import com.thfh.model.Course;
import com.thfh.model.User;
import com.thfh.model.UserCourseInteraction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserCourseInteractionRepository extends JpaRepository<UserCourseInteraction, Long>, JpaSpecificationExecutor<UserCourseInteraction> {
    Optional<UserCourseInteraction> findByUserAndCourse(User user, Course course);
    boolean existsByUserAndCourse(User user, Course course);
    
    /**
     * 查找点赞了指定课程的所有用户
     * @param course 课程
     * @return 点赞用户列表
     */
    List<UserCourseInteraction> findByCourseAndLikedTrue(Course course);
    
    /**
     * 查找收藏了指定课程的所有用户
     * @param course 课程
     * @return 收藏用户列表
     */
    List<UserCourseInteraction> findByCourseAndFavoritedTrue(Course course);

    /**
     * 查找用户收藏的所有课程
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页后的课程交互列表
     */
    Page<UserCourseInteraction> findByUserIdAndFavoritedTrue(Long userId, Pageable pageable);
}
package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.CourseDTO;
import com.thfh.dto.CourseInteractionDTO;
import com.thfh.dto.CourseQueryDTO;
import com.thfh.dto.SimpleUserDTO;
import com.thfh.model.User;
import com.thfh.service.CourseService;
import com.thfh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 课程管理控制器
 * 提供课程的增删改查和状态切换等功能
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    /**
     * 获取课程列表
     * @param queryDTO 查询条件，包含课程名称、类型和分页信息等
     * @return 课程分页列表
     */
    @GetMapping
    public Result<Page<CourseDTO>> getCourses(CourseQueryDTO queryDTO) {
        return Result.success(courseService.getCourses(queryDTO));
    }

    /**
     * 创建新课程
     * @param courseDTO 课程信息
     * @return 创建的课程信息
     */
    @PostMapping
    public Result<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        return Result.success(courseService.createCourse(courseDTO));
    }

    /**
     * 更新课程信息
     * @param id 课程ID
     * @param courseDTO 更新的课程信息
     * @return 更新后的课程信息
     */
    @PutMapping("/{id}")
    public Result<CourseDTO> updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDTO) {
        return Result.success(courseService.updateCourse(id, courseDTO));
    }

    /**
     * 删除课程
     * @param id 课程ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return Result.success(null);
    }

    /**
     * 切换课程状态（上线/下线）
     * @param id 课程ID
     * @return 操作结果
     */
    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleCourseStatus(@PathVariable Long id) {
        courseService.toggleCourseStatus(id);
        return Result.success(null);
    }

    /**
     * 获取课程的学生列表
     * @param id 课程ID
     * @return 学生列表，包含基本信息（ID、姓名、头像）
     */
    @GetMapping("/{id}/students")
    public Result<List<SimpleUserDTO>> getCourseStudents(@PathVariable Long id) {
        return Result.success(courseService.getCourseStudents(id));
    }

    /**
     * 学生加入课程
     * @param id 课程ID
     * @return 加入后的课程信息
     */
    @PostMapping("/{id}/enroll")
    public Result<CourseDTO> enrollCourse(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        return Result.success(courseService.enrollCourse(id, currentUser.getId()));
    }

    /**
     * 学生退出课程
     * @param id 课程ID
     * @return 操作结果
     */
    @PostMapping("/{id}/unenroll")
    public Result<Void> unenrollCourse(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        courseService.unenrollCourse(id, currentUser.getId());
        return Result.success(null);
    }

    /**
     * 点赞/取消点赞课程
     * @param id 课程ID
     * @return 操作结果
     */
    @PostMapping("/{id}/toggle-like")
    public Result<Void> toggleCourseLike(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        courseService.toggleCourseLike(id, currentUser.getId());
        return Result.success(null);
    }

    /**
     * 收藏/取消收藏课程
     * @param id 课程ID
     * @return 操作结果
     */
    @PostMapping("/{id}/toggle-favorite")
    public Result<Void> toggleCourseFavorite(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        courseService.toggleCourseFavorite(id, currentUser.getId());
        return Result.success(null);
    }

    /**
     * 获取当前用户对课程的交互信息（点赞和收藏状态）
     * @param id 课程ID
     * @return 交互信息，包含点赞和收藏状态
     */
    @GetMapping("/{id}/interaction")
    public Result<CourseInteractionDTO> getCourseInteractionInfo(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        return Result.success(courseService.getCourseInteractionInfo(id, currentUser.getId()));
    }

    /**
     * 获取课程的点赞和收藏用户列表
     * @param id 课程ID
     * @return 包含点赞和收藏用户列表的对象
     */
    @GetMapping("/{id}/interaction-users")
    public Result<Map<String, List<SimpleUserDTO>>> getCourseInteractionUsers(@PathVariable Long id) {
        return Result.success(courseService.getCourseInteractionUsers(id));
    }

    /**
     * 获取当前用户收藏的课程列表
     * @param page 页码
     * @param size 每页数量
     * @return 收藏的课程列表
     */
    @GetMapping("/favorites")
    public Result<Page<CourseDTO>> getFavoriteCourses(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        User currentUser = userService.getCurrentUser();
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(courseService.getUserFavoriteCourses(currentUser.getId(), pageRequest));
    }
}
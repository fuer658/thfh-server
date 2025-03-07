package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.CourseDTO;
import com.thfh.dto.CourseQueryDTO;
import com.thfh.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 课程管理控制器
 * 提供课程的增删改查和状态切换等功能
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

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
} 
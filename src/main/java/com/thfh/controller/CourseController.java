package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.CourseDTO;
import com.thfh.dto.CourseQueryDTO;
import com.thfh.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping
    public Result<Page<CourseDTO>> getCourses(CourseQueryDTO queryDTO) {
        return Result.success(courseService.getCourses(queryDTO));
    }

    @PostMapping
    public Result<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        return Result.success(courseService.createCourse(courseDTO));
    }

    @PutMapping("/{id}")
    public Result<CourseDTO> updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDTO) {
        return Result.success(courseService.updateCourse(id, courseDTO));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return Result.success(null);
    }

    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleCourseStatus(@PathVariable Long id) {
        courseService.toggleCourseStatus(id);
        return Result.success(null);
    }
} 
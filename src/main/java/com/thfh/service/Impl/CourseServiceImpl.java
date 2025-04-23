package com.thfh.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thfh.model.Course;
import com.thfh.model.CourseDetail;
import com.thfh.repository.CourseRepository;
import com.thfh.repository.CourseDetailRepository;
import com.thfh.service.CourseService;

import java.time.LocalDateTime;

@Service
public class CourseServiceImpl implements CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseDetailRepository courseDetailRepository;

    /**
     * 创建课程时自动创建对应的课程详情
     */
    @Override
    @Transactional
    public Course save(Course course) {
        Course savedCourse = courseRepository.save(course);
        
        // 自动创建对应的课程详情 - 直接使用repository而不是service
        try {
            // 检查课程详情是否已存在
            if (!courseDetailRepository.findByCourseId(savedCourse.getId()).isPresent()) {
                // 创建新的课程详情
                CourseDetail newDetail = new CourseDetail();
                newDetail.setCourseId(savedCourse.getId());
                newDetail.setCreateTime(LocalDateTime.now());
                newDetail.setUpdateTime(LocalDateTime.now());
                courseDetailRepository.save(newDetail);
            }
        } catch (Exception e) {
            log.error("创建课程详情失败", e);
        }
        
        return savedCourse;
    }
    
    /**
     * 删除课程时同时删除对应的课程详情
     */
    @Override
    @Transactional
    public void delete(Long id) {
        // 先获取课程详情，然后删除 - 直接使用repository而不是service
        try {
            courseDetailRepository.findByCourseId(id).ifPresent(courseDetail -> {
                courseDetailRepository.delete(courseDetail);
            });
        } catch (Exception e) {
            log.error("删除课程详情失败", e);
        }
        
        courseRepository.deleteById(id);
    }
} 
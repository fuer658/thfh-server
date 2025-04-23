package com.thfh.service;

import com.thfh.dto.*;
import com.thfh.model.*;
import com.thfh.repository.CourseRepository;
import com.thfh.repository.CourseDetailRepository;
import com.thfh.repository.UserCourseInteractionRepository;
import com.thfh.repository.UserCourseRepository;
import com.thfh.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.Predicate;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 课程服务
 */
@Service
public class CourseService {
    
    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseDetailRepository courseDetailRepository;
    
    /**
     * 创建课程时自动创建对应的课程详情
     */
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

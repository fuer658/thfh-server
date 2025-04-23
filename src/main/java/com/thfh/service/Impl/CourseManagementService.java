package com.thfh.service.impl;

import com.thfh.dto.*;
import com.thfh.model.*;
import com.thfh.repository.CourseRepository;
import com.thfh.repository.UserCourseInteractionRepository;
import com.thfh.repository.UserCourseRepository;
import com.thfh.repository.UserRepository;
import com.thfh.service.CourseTagService;
import com.thfh.service.UserService;
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

import javax.persistence.criteria.Predicate;
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
 * 课程管理服务类
 * 提供课程相关的业务逻辑处理，包括课程的创建、查询、修改、删除等操作
 * 以及课程状态管理等功能
 */
@Service
public class CourseManagementService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    // 允许的图片类型
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp", "image/svg+xml"
    );
    
    // 允许的视频类型
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/quicktime", "video/x-msvideo", "video/x-matroska", "video/webm", "video/ogg"
    );
    
    // 允许的文档类型
    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
            "application/pdf", "application/msword", 
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain"
    );
    
    @Value("${file.upload-dir}")
    private String baseUploadDir;
    
    @Value("${server.port}")
    private String serverPort;
    
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCourseInteractionRepository userCourseInteractionRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private CourseTagService courseTagService;
    
    @Autowired
    private UserService userService;

    /**
     * 根据查询条件获取课程列表
     * @param queryDTO 查询条件对象，包含课程标题、教员ID、状态、启用状态等过滤条件
     * @return 分页后的课程DTO列表
     */
    public Page<CourseDTO> getCourses(CourseQueryDTO queryDTO) {
        Specification<Course> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (queryDTO.getTitle() != null) {
                predicates.add(cb.like(root.get("title"), "%" + queryDTO.getTitle() + "%"));
            }
            if (queryDTO.getTeacherId() != null) {
                predicates.add(cb.equal(root.get("teacher").get("id"), queryDTO.getTeacherId()));
            }
            if (queryDTO.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), queryDTO.getStatus()));
            }
            if (queryDTO.getEnabled() != null) {
                predicates.add(cb.equal(root.get("enabled"), queryDTO.getEnabled()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Course> coursePage = courseRepository.findAll(spec, 
            PageRequest.of(queryDTO.getPageNum() - 1, queryDTO.getPageSize()));
        
        return coursePage.map(this::convertToDTO);
    }

    /**
     * 创建新课程
     * @param courseDTO 课程信息对象，包含课程的基本信息
     * @return 创建成功的课程DTO对象
     * @throws RuntimeException 当必填字段为空、教员不存在或已存在同名课程时抛出
     */
    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO) {
        // 验证必填字段
        if (courseDTO.getTitle() == null || courseDTO.getTitle().trim().isEmpty()) {
            throw new RuntimeException("课程标题不能为空");
        }
        if (courseDTO.getCoverImage() == null || courseDTO.getCoverImage().trim().isEmpty()) {
            throw new RuntimeException("课程封面不能为空");
        }
        if (courseDTO.getPrice() == null) {
            throw new RuntimeException("课程价格不能为空");
        }
        if (courseDTO.getTotalHours() == null || courseDTO.getTotalHours() <= 0) {
            throw new RuntimeException("课程总时长必须大于0");
        }
        if (courseDTO.getTeacherId() == null) {
            throw new RuntimeException("教员ID不能为空");
        }

        if (courseRepository.existsByTitleAndTeacherId(courseDTO.getTitle(), courseDTO.getTeacherId())) {
            throw new RuntimeException("该教员已存在同名课程");
        }

        User teacher = userRepository.findById(courseDTO.getTeacherId())
                .orElseThrow(() -> new RuntimeException("教员不存在"));

        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        
        // 设置必要的默认值
        course.setStatus(CourseStatus.DRAFT); // 新创建的课程默认为草稿状态
        course.setTeacher(teacher);
        course.setLikeCount(0);
        course.setFavoriteCount(0);
        course.setStudentCount(0);
        course.setEnabled(true);
        
        // 处理课程标签
        if (courseDTO.getTags() != null) {
            course.setTags(courseTagService.processTags(courseDTO.getTags()));
        }
        
        course = courseRepository.save(course);
        
        return convertToDTO(course);
    }

    /**
     * 更新课程信息
     * @param id 课程ID
     * @param courseDTO 更新后的课程信息对象
     * @return 更新后的课程DTO对象
     * @throws RuntimeException 当课程不存在时抛出
     */
    @Transactional
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        // 验证必填字段
        if (courseDTO.getTitle() == null || courseDTO.getTitle().trim().isEmpty()) {
            throw new RuntimeException("课程标题不能为空");
        }
        if (courseDTO.getCoverImage() == null || courseDTO.getCoverImage().trim().isEmpty()) {
            throw new RuntimeException("课程封面不能为空");
        }
        if (courseDTO.getPrice() == null) {
            throw new RuntimeException("课程价格不能为空");
        }
        if (courseDTO.getTotalHours() == null || courseDTO.getTotalHours() <= 0) {
            throw new RuntimeException("课程总时长必须大于0");
        }

        // 更新课程信息
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setCoverImage(courseDTO.getCoverImage());
        course.setCoverVideo(courseDTO.getCoverVideo());
        course.setPrice(courseDTO.getPrice());
        course.setPointsPrice(courseDTO.getPointsPrice());
        course.setTotalHours(courseDTO.getTotalHours());
        course.setStatus(courseDTO.getStatus());
        course.setVideoUrl(courseDTO.getVideoUrl());
        course.setMaterials(courseDTO.getMaterials());
        // 如果enabled为null，保持原值
        if (courseDTO.getEnabled() != null) {
            course.setEnabled(courseDTO.getEnabled());
        }
        course.setUpdateTime(LocalDateTime.now());

        // 处理课程标签
        if (courseDTO.getTags() != null) {
            course.setTags(courseTagService.processTags(courseDTO.getTags()));
        }

        course = courseRepository.save(course);
        return convertToDTO(course);
    }

    /**
     * 删除指定ID的课程
     * @param id 要删除的课程ID
     */
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    /**
     * 切换课程启用状态
     * 如果课程当前是启用状态，则禁用；如果是禁用状态，则启用
     * @param id 课程ID
     * @throws RuntimeException 当课程不存在时抛出
     */
    public void toggleCourseStatus(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        course.setEnabled(!course.getEnabled());
        courseRepository.save(course);
    }

    /**
     * 将Course实体转换为CourseDTO
     * @param course 课程实体
     * @return 课程DTO对象
     */
    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        BeanUtils.copyProperties(course, dto);
        
        // 设置讲师信息
        if (course.getTeacher() != null) {
            dto.setTeacherId(course.getTeacher().getId());
            dto.setTeacherName(course.getTeacher().getUsername());
        }
        
        // 设置标签信息
        if (course.getTags() != null) {
            Set<CourseTagDTO> tagDTOs = course.getTags().stream()
                .map(tag -> {
                    CourseTagDTO tagDTO = new CourseTagDTO();
                    BeanUtils.copyProperties(tag, tagDTO);
                    return tagDTO;
                })
                .collect(Collectors.toSet());
            dto.setTags(tagDTOs);
        }
        
        // 设置时间格式
        if (course.getCreateTime() != null) {
            dto.setCreateTime(course.getCreateTime().toString());
        }
        
        return dto;
    }
} 
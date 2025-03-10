package com.thfh.service;

import com.thfh.dto.CourseDTO;
import com.thfh.dto.CourseQueryDTO;
import com.thfh.model.Course;
import com.thfh.model.CourseStatus;
import com.thfh.model.User;
import com.thfh.repository.CourseRepository;
import com.thfh.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程服务类
 * 提供课程相关的业务逻辑处理，包括课程的创建、查询、修改、删除等操作
 * 以及课程状态管理等功能
 */
@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

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

        BeanUtils.copyProperties(courseDTO, course, "id", "teacher", "createTime");
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
     * 将课程实体对象转换为DTO对象
     * @param course 课程实体对象
     * @return 转换后的课程DTO对象
     */
    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        BeanUtils.copyProperties(course, dto);
        dto.setTeacherId(course.getTeacher().getId());
        dto.setTeacherName(course.getTeacher().getRealName());
        return dto;
    }
}

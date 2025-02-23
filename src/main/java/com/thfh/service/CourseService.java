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

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

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

    @Transactional
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        BeanUtils.copyProperties(courseDTO, course, "id", "teacher", "createTime");
        course = courseRepository.save(course);
        
        return convertToDTO(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public void toggleCourseStatus(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        course.setEnabled(!course.getEnabled());
        courseRepository.save(course);
    }

    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        BeanUtils.copyProperties(course, dto);
        dto.setTeacherId(course.getTeacher().getId());
        dto.setTeacherName(course.getTeacher().getRealName());
        return dto;
    }
}

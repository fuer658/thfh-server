package com.thfh.service;

import com.thfh.dto.CourseDTO;
import com.thfh.dto.CourseInteractionDTO;
import com.thfh.dto.CourseQueryDTO;
import com.thfh.dto.SimpleUserDTO;
import com.thfh.model.*;
import com.thfh.repository.CourseRepository;
import com.thfh.repository.UserCourseInteractionRepository;
import com.thfh.repository.UserCourseRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private UserCourseInteractionRepository userCourseInteractionRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;

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

    @Transactional
    public void toggleCourseLike(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        UserCourseInteraction interaction = userCourseInteractionRepository
                .findByUserAndCourse(user, course)
                .orElseGet(() -> {
                    UserCourseInteraction newInteraction = new UserCourseInteraction();
                    newInteraction.setUser(user);
                    newInteraction.setCourse(course);
                    return newInteraction;
                });

        boolean newLikeStatus = !interaction.getLiked();
        interaction.setLiked(newLikeStatus);
        userCourseInteractionRepository.save(interaction);

        // 更新课程点赞数
        course.setLikeCount(course.getLikeCount() + (newLikeStatus ? 1 : -1));
        courseRepository.save(course);
    }

    @Transactional
    public void toggleCourseFavorite(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        UserCourseInteraction interaction = userCourseInteractionRepository
                .findByUserAndCourse(user, course)
                .orElseGet(() -> {
                    UserCourseInteraction newInteraction = new UserCourseInteraction();
                    newInteraction.setUser(user);
                    newInteraction.setCourse(course);
                    return newInteraction;
                });

        boolean newFavoriteStatus = !interaction.getFavorited();
        interaction.setFavorited(newFavoriteStatus);
        userCourseInteractionRepository.save(interaction);

        // 更新课程收藏数
        course.setFavoriteCount(course.getFavoriteCount() + (newFavoriteStatus ? 1 : -1));
        courseRepository.save(course);
    }

    /**
     * 学生加入课程
     * @param courseId 课程ID
     * @param userId 学生ID
     * @return 加入后的课程信息
     */
    public CourseDTO enrollCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        if (!course.getEnabled()) {
            throw new RuntimeException("课程未启用，无法加入");
        }

        User student = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (student.getUserType() != UserType.STUDENT) {
            throw new RuntimeException("只有学生用户才能加入课程");
        }

        if (userCourseRepository.existsByUserAndCourse(student, course)) {
            throw new RuntimeException("您已经加入过该课程");
        }

        UserCourse userCourse = new UserCourse();
        userCourse.setUser(student);
        userCourse.setCourse(course);
        userCourseRepository.save(userCourse);

        // 更新课程学习人数
        course.setStudentCount(course.getStudentCount() + 1);
        courseRepository.save(course);

        return convertToDTO(course);
    }

    /**
     * 学生退出课程
     * @param courseId 课程ID
     * @param userId 学生ID
     */
    public void unenrollCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        User student = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        UserCourse userCourse = userCourseRepository.findByUserAndCourse(student, course)
                .orElseThrow(() -> new RuntimeException("您尚未加入该课程"));

        userCourseRepository.delete(userCourse);

        // 更新课程学习人数
        course.setStudentCount(Math.max(0, course.getStudentCount() - 1));
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

    /**
     * 获取课程的学生列表
     * @param courseId 课程ID
     * @return 学生列表，包含基本信息（ID、姓名、头像）
     */
    public List<SimpleUserDTO> getCourseStudents(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        List<UserCourse> userCourses = userCourseRepository.findByCourse(course);
        return userCourses.stream()
                .map(uc -> SimpleUserDTO.fromEntity(uc.getUser()))
                .collect(Collectors.toList());
    }

    /**
     * 获取用户对课程的交互信息（点赞和收藏状态）
     * @param courseId 课程ID
     * @param userId 用户ID
     * @return 包含点赞和收藏状态的对象，只有为true的状态才会包含在返回结果中
     */
    public CourseInteractionDTO getCourseInteractionInfo(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        CourseInteractionDTO interactionDTO = new CourseInteractionDTO();
        userCourseInteractionRepository.findByUserAndCourse(user, course)
                .ifPresent(interaction -> {
                    if (interaction.getLiked()) {
                        interactionDTO.setLiked(true);
                    }
                    if (interaction.getFavorited()) {
                        interactionDTO.setFavorited(true);
                    }
                });

        return interactionDTO;
    }

    /**
     * 获取课程的点赞和收藏用户列表
     * @param courseId 课程ID
     * @return 包含点赞和收藏用户列表的对象
     */
    public Map<String, List<SimpleUserDTO>> getCourseInteractionUsers(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        List<SimpleUserDTO> likedUsers = userCourseInteractionRepository.findByCourseAndLikedTrue(course)
                .stream()
                .map(interaction -> SimpleUserDTO.fromEntity(interaction.getUser()))
                .collect(Collectors.toList());

        List<SimpleUserDTO> favoritedUsers = userCourseInteractionRepository.findByCourseAndFavoritedTrue(course)
                .stream()
                .map(interaction -> SimpleUserDTO.fromEntity(interaction.getUser()))
                .collect(Collectors.toList());

        Map<String, List<SimpleUserDTO>> result = new HashMap<>();
        result.put("likedUsers", likedUsers);
        result.put("favoritedUsers", favoritedUsers);
        return result;
    }
}

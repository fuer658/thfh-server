package com.thfh.service.impl;

import com.thfh.dto.*;
import com.thfh.model.*;
import com.thfh.repository.CourseRepository;
import com.thfh.repository.UserCourseInteractionRepository;
import com.thfh.repository.UserCourseRepository;
import com.thfh.repository.UserRepository;
import com.thfh.service.CourseManagementService;
import com.thfh.service.CourseTagService;
import com.thfh.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 课程管理服务实现类
 * 提供课程相关的业务逻辑处理，包括课程的创建、查询、修改、删除等操作
 * 以及课程状态管理等功能
 */
@Service
public class CourseManagementServiceImpl implements CourseManagementService {
    
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    /**
     * 切换课程启用状态
     * 如果课程当前是启用状态，则禁用；如果是禁用状态，则启用
     * @param id 课程ID
     * @throws RuntimeException 当课程不存在时抛出
     */
    @Override
    public void toggleCourseStatus(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        course.setEnabled(!course.getEnabled());
        courseRepository.save(course);
    }
    
    /**
     * 点赞/取消点赞课程
     * @param courseId 课程ID
     * @param userId 用户ID
     */
    @Override
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

    /**
     * 收藏/取消收藏课程
     * @param courseId 课程ID
     * @param userId 用户ID
     */
    @Override
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
    @Override
    @Transactional
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
    @Override
    @Transactional
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
     * 获取课程的学生列表
     * @param courseId 课程ID
     * @return 学生列表，包含基本信息（ID、姓名、头像）
     */
    @Override
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
    @Override
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
    @Override
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

    /**
     * 获取用户收藏的课程列表
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页后的课程列表
     */
    @Override
    public Page<CourseDTO> getUserFavoriteCourses(Long userId, Pageable pageable) {
        Page<UserCourseInteraction> interactions = userCourseInteractionRepository
                .findByUserIdAndFavoritedTrue(userId, pageable);
        
        return interactions.map(interaction -> convertToDTO(interaction.getCourse()));
    }
    
    /**
     * 获取课程详情
     * @param id 课程ID
     * @return 课程详情
     */
    @Override
    public CourseDTO getCourseDetail(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        
        // 如果课程状态为草稿，只有课程创建者可以查看
        if (course.getStatus() == CourseStatus.DRAFT) {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null || !course.getTeacher().getId().equals(currentUser.getId())) {
                throw new RuntimeException("该课程尚未发布，无法查看");
            }
        }
        
        // 转换为DTO并返回
        return convertToDTO(course);
    }
    
    /**
     * 发布课程
     * @param id 课程ID
     * @return 发布后的课程信息
     */
    @Override
    @Transactional
    public CourseDTO publishCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        
        // 验证当前用户是否为课程创建者
        User currentUser = userService.getCurrentUser();
        if (currentUser == null || !course.getTeacher().getId().equals(currentUser.getId())) {
            throw new RuntimeException("只有课程创建者才能发布课程");
        }
        
        // 验证课程必要信息是否完整
        if (course.getTitle() == null || course.getTitle().trim().isEmpty()) {
            throw new RuntimeException("课程标题不能为空");
        }
        if (course.getCoverImage() == null || course.getCoverImage().trim().isEmpty()) {
            throw new RuntimeException("课程封面不能为空");
        }
        if (course.getDescription() == null || course.getDescription().trim().isEmpty()) {
            throw new RuntimeException("课程描述不能为空");
        }
        if (course.getPrice() == null) {
            throw new RuntimeException("课程价格不能为空");
        }
        if (course.getTotalHours() == null || course.getTotalHours() <= 0) {
            throw new RuntimeException("课程总时长必须大于0");
        }
        
        // 更新课程状态为已发布
        course.setStatus(CourseStatus.PUBLISHED);
        course.setUpdateTime(LocalDateTime.now());
        course = courseRepository.save(course);
        
        return convertToDTO(course);
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
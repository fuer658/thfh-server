package com.thfh.service;

import com.thfh.dto.CourseDTO;
import com.thfh.dto.CourseInteractionDTO;
import com.thfh.dto.CourseQueryDTO;
import com.thfh.dto.CourseTagDTO;
import com.thfh.dto.SimpleUserDTO;
import com.thfh.dto.PointsRecordDTO;
import com.thfh.model.Course;
import com.thfh.model.CourseStatus;
import com.thfh.model.User;
import com.thfh.model.UserCourse;
import com.thfh.model.UserCourseInteraction;
import com.thfh.model.UserType;
import com.thfh.repository.CourseRepository;
import com.thfh.repository.UserCourseInteractionRepository;
import com.thfh.repository.UserCourseRepository;
import com.thfh.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.thfh.exception.BusinessException;
import com.thfh.exception.ErrorCode;

/**
 * 课程管理服务
 * 提供课程相关的业务逻辑处理，包括课程的创建、查询、修改、删除等操作
 * 以及课程状态管理等功能
 */
@Service
public class CourseManagementService {
    private final String baseUploadDir;
    private final String serverPort;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UserCourseInteractionRepository userCourseInteractionRepository;
    private final UserCourseRepository userCourseRepository;
    private final CourseTagService courseTagService;
    private final UserService userService;
    private final PointsService pointsService;

    // 魔法字符串常量
    private static final String COURSE_NOT_FOUND = "课程不存在";
    private static final String USER_NOT_FOUND = "用户不存在";
    private static final String COURSE_TITLE_EMPTY = "课程标题不能为空";
    private static final String COURSE_COVER_EMPTY = "课程封面不能为空";
    private static final String COURSE_PRICE_EMPTY = "课程价格不能为空";
    private static final String COURSE_TOTAL_HOURS_INVALID = "课程总时长必须大于0";
    private static final String TEACHER_ID_EMPTY = "教员ID不能为空";
    private static final String TEACHER_NOT_FOUND = "教员不存在";
    private static final String DUPLICATE_COURSE = "该教员已存在同名课程";
    private static final String COURSE_NOT_ENABLED = "课程未启用，无法加入";
    private static final String ONLY_STUDENT_CAN_JOIN = "只有学生用户才能加入课程";
    private static final String ALREADY_JOINED = "您已经加入过该课程";
    private static final String NOT_JOINED = "您尚未加入该课程";
    private static final String ONLY_CREATOR_CAN_PUBLISH = "只有课程创建者才能发布课程";
    private static final String COURSE_NOT_PUBLISHED = "该课程尚未发布，无法查看";
    private static final String COURSE_DESC_EMPTY = "课程描述不能为空";
    private static final String ALREADY_PURCHASED = "您已经购买或加入过该课程";
    private static final String POINTS_NOT_ENOUGH = "积分不足，无法购买该课程";
    private static final String POINTS_NOT_SUPPORT = "该课程不支持积分购买";
    private static final String VIEW_COUNT = "viewCount";
    private static final String LIKE_COUNT = "likeCount";
    private static final String FAVORITE_COUNT = "favoriteCount";
    private static final String STUDENT_COUNT = "studentCount";

    public CourseManagementService(
            @Value("${file.upload-dir}") String baseUploadDir,
            @Value("${server.port}") String serverPort,
            CourseRepository courseRepository,
            UserRepository userRepository,
            UserCourseInteractionRepository userCourseInteractionRepository,
            UserCourseRepository userCourseRepository,
            CourseTagService courseTagService,
            UserService userService,
            PointsService pointsService) {
        this.baseUploadDir = baseUploadDir;
        this.serverPort = serverPort;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.userCourseInteractionRepository = userCourseInteractionRepository;
        this.userCourseRepository = userCourseRepository;
        this.courseTagService = courseTagService;
        this.userService = userService;
        this.pointsService = pointsService;
    }
    
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
        if (courseDTO.getTitle() == null || courseDTO.getTitle().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, COURSE_TITLE_EMPTY);
        }
        if (courseDTO.getCoverImage() == null || courseDTO.getCoverImage().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, COURSE_COVER_EMPTY);
        }
        if (courseDTO.getPrice() == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, COURSE_PRICE_EMPTY);
        }
        if (courseDTO.getTotalHours() == null || courseDTO.getTotalHours() <= 0) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, COURSE_TOTAL_HOURS_INVALID);
        }
        if (courseDTO.getTeacherId() == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, TEACHER_ID_EMPTY);
        }
        if (courseRepository.existsByTitleAndTeacherId(courseDTO.getTitle(), courseDTO.getTeacherId())) {
            throw new BusinessException(ErrorCode.CONFLICT, DUPLICATE_COURSE);
        }
        User teacher = userRepository.findById(courseDTO.getTeacherId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXIST, TEACHER_NOT_FOUND));

        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        
        // 设置必要的默认值
        // 如果前端传递了状态，则使用前端传递的状态，否则默认为草稿状态
        if (courseDTO.getStatus() == null) {
            course.setStatus(CourseStatus.DRAFT); // 新创建的课程默认为草稿状态
        } else {
            course.setStatus(courseDTO.getStatus()); // 使用前端传递的状态
        }
        
        course.setTeacher(teacher);
        course.setLikeCount(0);
        course.setFavoriteCount(0);
        course.setStudentCount(0);
        course.setEnabled(true);
        
        // 处理课程标签
        if (courseDTO.getTags() != null) {
            course.setTags(courseTagService.processTags(courseDTO.getTags()));
        }
        
        // 新增：设置开发团队字段
        course.setDevTeam(courseDTO.getDevTeam());
        
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
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_EXIST, COURSE_NOT_FOUND));

        if (courseDTO.getTitle() == null || courseDTO.getTitle().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, COURSE_TITLE_EMPTY);
        }
        if (courseDTO.getCoverImage() == null || courseDTO.getCoverImage().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, COURSE_COVER_EMPTY);
        }
        if (courseDTO.getPrice() == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, COURSE_PRICE_EMPTY);
        }
        if (courseDTO.getTotalHours() == null || courseDTO.getTotalHours() <= 0) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, COURSE_TOTAL_HOURS_INVALID);
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
        // 新增：更新开发团队字段
        course.setDevTeam(courseDTO.getDevTeam());
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
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_EXIST, COURSE_NOT_FOUND));
        course.setEnabled(!course.getEnabled());
        courseRepository.save(course);
    }
    
    /**
     * 点赞/取消点赞课程
     * @param courseId 课程ID
     * @param userId 用户ID
     */
    @Transactional
    public void toggleCourseLike(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_EXIST, COURSE_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXIST, USER_NOT_FOUND));

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
    @Transactional
    public void toggleCourseFavorite(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_EXIST, COURSE_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXIST, USER_NOT_FOUND));

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
    @Transactional
    public CourseDTO enrollCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_EXIST, COURSE_NOT_FOUND));

        if (!course.getEnabled()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, COURSE_NOT_ENABLED);
        }

        User student = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXIST, USER_NOT_FOUND));

        if (student.getUserType() != UserType.STUDENT) {
            throw new BusinessException(ErrorCode.FORBIDDEN, ONLY_STUDENT_CAN_JOIN);
        }

        if (userCourseRepository.existsByUserAndCourse(student, course)) {
            throw new BusinessException(ErrorCode.CONFLICT, ALREADY_JOINED);
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
    @Transactional
    public void unenrollCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_EXIST, COURSE_NOT_FOUND));

        User student = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXIST, USER_NOT_FOUND));

        UserCourse userCourse = userCourseRepository.findByUserAndCourse(student, course)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_EXIST, NOT_JOINED));

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
    public List<SimpleUserDTO> getCourseStudents(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_EXIST, COURSE_NOT_FOUND));

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
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_EXIST, COURSE_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXIST, USER_NOT_FOUND));

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
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_EXIST, COURSE_NOT_FOUND));

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
    @Transactional
    public CourseDTO getCourseDetail(Long id) {
        courseRepository.increaseViewCountById(id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_EXIST, COURSE_NOT_FOUND));
        // 如果课程状态为草稿，只有课程创建者可以查看
        if (course.getStatus() == CourseStatus.DRAFT) {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null || !course.getTeacher().getId().equals(currentUser.getId())) {
                throw new BusinessException(ErrorCode.FORBIDDEN, COURSE_NOT_PUBLISHED);
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
    @Transactional
    public CourseDTO publishCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_EXIST, COURSE_NOT_FOUND));
        
        // 验证当前用户是否为课程创建者
        User currentUser = userService.getCurrentUser();
        if (currentUser == null || !course.getTeacher().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, ONLY_CREATOR_CAN_PUBLISH);
        }
        
        // 验证课程必要信息是否完整
        if (course.getTitle() == null || course.getTitle().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, COURSE_TITLE_EMPTY);
        }
        if (course.getCoverImage() == null || course.getCoverImage().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, COURSE_COVER_EMPTY);
        }
        if (course.getDescription() == null || course.getDescription().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, COURSE_DESC_EMPTY);
        }
        if (course.getPrice() == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, COURSE_PRICE_EMPTY);
        }
        if (course.getTotalHours() == null || course.getTotalHours() <= 0) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, COURSE_TOTAL_HOURS_INVALID);
        }
        
        // 更新课程状态为已发布
        course.setStatus(CourseStatus.PUBLISHED);
        course.setUpdateTime(LocalDateTime.now());
        course = courseRepository.save(course);
        
        return convertToDTO(course);
    }

    /**
     * 获取热门课程分页列表
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @param sortBy 排序字段（viewCount/likeCount/favoriteCount/studentCount），默认viewCount
     * @return 分页后的热门课程DTO列表
     */
    public Page<CourseDTO> getHotCourses(int page, int size, String sortBy) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        if (sortBy == null || sortBy.isEmpty()) sortBy = VIEW_COUNT;
        if (!sortBy.equals(VIEW_COUNT) && !sortBy.equals(LIKE_COUNT) && !sortBy.equals(FAVORITE_COUNT) && !sortBy.equals(STUDENT_COUNT)) {
            sortBy = VIEW_COUNT;
        }
        Pageable pageable = PageRequest.of(page - 1, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, sortBy));
        // 只查已发布且启用的课程
        Page<Course> coursePage = courseRepository.findAllByStatusAndEnabledTrue(CourseStatus.PUBLISHED, pageable);
        return coursePage.map(this::convertToDTO);
    }

    /**
     * 积分购买课程
     * @param courseId 课程ID
     * @param userId 用户ID
     * @return 积分扣除记录DTO
     */
    @Transactional
    public PointsRecordDTO purchaseCourseByPoints(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_EXIST, COURSE_NOT_FOUND));
        if (!course.getEnabled()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, COURSE_NOT_ENABLED);
        }
        if (course.getPointsPrice() == null || course.getPointsPrice() <= 0) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, POINTS_NOT_SUPPORT);
        }
        User student = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXIST, USER_NOT_FOUND));
        if (student.getUserType() != UserType.STUDENT) {
            throw new BusinessException(ErrorCode.FORBIDDEN, ONLY_STUDENT_CAN_JOIN);
        }
        if (userCourseRepository.existsByUserAndCourse(student, course)) {
            throw new BusinessException(ErrorCode.CONFLICT, ALREADY_PURCHASED);
        }
        Integer currentPoints = student.getPoints();
        if (currentPoints == null || currentPoints < course.getPointsPrice()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, POINTS_NOT_ENOUGH);
        }
        // 扣除积分并记录
        com.thfh.dto.PointsAdjustDTO adjustDTO = new com.thfh.dto.PointsAdjustDTO();
        adjustDTO.setStudentId(userId);
        adjustDTO.setPoints(-course.getPointsPrice());
        adjustDTO.setDescription("积分购买课程：" + course.getTitle());
        adjustDTO.setIncludeExperience(false);
        com.thfh.dto.PointsRecordDTO recordDTO = pointsService.adjustPoints(adjustDTO);
        // 添加选课记录
        UserCourse userCourse = new UserCourse();
        userCourse.setUser(student);
        userCourse.setCourse(course);
        userCourseRepository.save(userCourse);
        // 更新课程学习人数
        course.setStudentCount(course.getStudentCount() + 1);
        courseRepository.save(course);
        return recordDTO;
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
        
        // 新增：设置开发团队字段
        dto.setDevTeam(course.getDevTeam());
        
        return dto;
    }
} 
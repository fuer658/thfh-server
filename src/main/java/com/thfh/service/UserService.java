package com.thfh.service;

import com.thfh.dto.CompanyDetails;
import com.thfh.dto.UserDTO;
import com.thfh.dto.UserQueryDTO;
import com.thfh.dto.LoginDTO;
import com.thfh.exception.ResourceNotFoundException;
import com.thfh.model.*;
import com.thfh.repository.CompanyRepository;
import com.thfh.repository.UserInterestRepository;
import com.thfh.repository.UserRepository;
import com.thfh.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;
import com.thfh.exception.UserNotLoggedInException;

/**
 * 用户服务类
 * 提供用户相关的业务逻辑处理，包括用户的创建、查询、修改、删除等操作
 * 以及用户登录、状态管理等功能
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserInterestRepository userInterestRepository;
    private final CompanyRepository companyRepository;

    private static final String USER_NOT_FOUND_MESSAGE = "用户不存在";

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserInterestRepository userInterestRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userInterestRepository = userInterestRepository;
        this.companyRepository = companyRepository;
    }

    // 用户等级经验值常量
    public static final int LEVEL_1_TO_2_EXP = 100;
    public static final int LEVEL_2_TO_3_EXP = 300;
    public static final int LEVEL_3_TO_4_EXP = 600;
    public static final int LEVEL_4_TO_5_EXP = 1200;
    public static final int MAX_LEVEL = 5;

    /**
     * 根据查询条件获取用户列表
     * @param queryDTO 查询条件对象，包含用户类型、用户名、真实姓名、启用状态等过滤条件
     * @return 分页后的用户DTO列表
     */
    public Page<UserDTO> getUsers(UserQueryDTO queryDTO) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (queryDTO.getUserType() != null) {
                predicates.add(cb.equal(root.get("userType"), queryDTO.getUserType()));
            }
            if (queryDTO.getUsername() != null) {
                predicates.add(cb.like(root.get("username"), "%" + queryDTO.getUsername() + "%"));
            }
            if (queryDTO.getRealName() != null) {
                predicates.add(cb.like(root.get("realName"), "%" + queryDTO.getRealName() + "%"));
            }
            if (queryDTO.getEnabled() != null) {
                predicates.add(cb.equal(root.get("enabled"), queryDTO.getEnabled()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> userPage = userRepository.findAll(spec,
                 PageRequest.of(queryDTO.getPageNum() - 1, queryDTO.getPageSize()));

        // 批量获取用户兴趣
        List<Long> userIds = userPage.getContent().stream()
                                     .map(User::getId)
                                     .collect(Collectors.toList());
        List<UserInterest> userInterests = userInterestRepository.findByUserIdIn(userIds);
        
        // 将兴趣映射到用户ID
        Map<Long, List<InterestType>> userInterestMap = userInterests.stream()
                .collect(Collectors.groupingBy(ui -> ui.getUser().getId(),
                                               Collectors.mapping(UserInterest::getInterestType, Collectors.toList())));

        return userPage.map(user -> convertToDTO(user, userInterestMap.getOrDefault(user.getId(), new ArrayList<>())));
     }

    /**
     * 检查当前用户是否有权限操作目标用户
     * @param currentUser 当前用户
     * @param targetUser 目标用户
     * @return 是否有权限
     */
    private boolean hasPermission(User currentUser, User targetUser) {
        // 教员可以管理学员账号
        if (currentUser.getUserType() == UserType.TEACHER && targetUser.getUserType() == UserType.STUDENT) {
            return true;
        }
        // 用户只能操作自己的账号
        return currentUser.getId().equals(targetUser.getId());
    }

    /**
     * 验证用户数据
     * @param userDTO 用户数据
     * @throws RuntimeException 当数据验证失败时抛出
     */
    private void validateUserData(UserDTO userDTO) {
        List<String> errors = new ArrayList<>();

        // 验证用户名
        if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
            errors.add("用户名不能为空");
        } else if (userDTO.getUsername().length() < 3 || userDTO.getUsername().length() > 50) {
            errors.add("用户名长度必须在3-50个字符之间");
        }

        // 验证密码（仅在创建用户时）
        if (userDTO.getId() == null && (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty())) {
            errors.add("密码不能为空");
        }

        // 验证用户类型
        if (userDTO.getUserType() == null) {
            errors.add("用户类型不能为空");
        }

        // 验证性别
        if (userDTO.getGender() == null) {
            userDTO.setGender(Gender.UNKNOWN);
        }

        // 验证邮箱格式
        if (userDTO.getEmail() != null && !userDTO.getEmail().trim().isEmpty()) {
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (!userDTO.getEmail().matches(emailRegex)) {
                errors.add("邮箱格式不正确");
            }
        }

        // 验证手机号格式
        if (userDTO.getPhone() != null && !userDTO.getPhone().trim().isEmpty()) {
            String phoneRegex = "^1[3-9]\\d{9}$";
            if (!userDTO.getPhone().matches(phoneRegex)) {
                errors.add("手机号格式不正确");
            }
        }

        if (!errors.isEmpty()) {
            throw new RuntimeException(String.join(", ", errors));
        }
    }

    /**
     * 创建新用户
     * @param userDTO 用户信息对象，包含用户的基本信息
     * @return 创建成功的用户DTO对象
     * @throws RuntimeException 当用户名已存在时抛出
     */
    public UserDTO createUser(UserDTO userDTO) {
        // 数据验证
        validateUserData(userDTO);

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        BeanUtils.copyProperties(userDTO, user, "id", "birthday", "createTime", "updateTime", "lastLoginTime");

        // 处理生日日期
        if (userDTO.getBirthday() != null && !userDTO.getBirthday().isEmpty()) {
            try {
                user.setBirthday(LocalDate.parse(userDTO.getBirthday()).atStartOfDay());
            } catch (Exception e) {
                throw new RuntimeException("生日日期格式不正确，请使用yyyy-MM-dd格式");
            }
        }

        // 设置默认值
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEnabled(true);
        user.setExperience(0);
        user.setLevel(1);
        user.setPoints(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        user = userRepository.save(user);
        return convertToDTO(user);
    }

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param userDTO 更新后的用户信息对象
     * @return 更新后的用户DTO对象
     * @throws RuntimeException 当用户不存在时抛出
     */
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        // 检查当前用户权限
        User currentUser = getCurrentUser();
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE));

        if (!hasPermission(currentUser, targetUser)) {
            throw new RuntimeException("没有权限修改该用户信息");
        }

        // 数据验证
        validateUserData(userDTO);

        // 如果要修改用户名，检查新用户名是否已存在
        if (!targetUser.getUsername().equals(userDTO.getUsername()) &&
            userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("新用户名已存在");
        }

        // 复制属性，排除敏感字段
        BeanUtils.copyProperties(userDTO, targetUser, "id", "password", "createTime", "updateTime", "lastLoginTime", "birthday");

        // 处理密码更新
        if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
            targetUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        // 处理生日日期
        if (userDTO.getBirthday() != null && !userDTO.getBirthday().isEmpty()) {
            try {
                targetUser.setBirthday(LocalDate.parse(userDTO.getBirthday()).atStartOfDay());
            } catch (Exception e) {
                throw new RuntimeException("生日日期格式不正确，请使用yyyy-MM-dd格式");
            }
        }

        targetUser.setUpdateTime(LocalDateTime.now());
        targetUser = userRepository.save(targetUser);

        return convertToDTO(targetUser);
    }

    /**
     * 删除指定ID的用户
     * @param id 要删除的用户ID
     */
    public void deleteUser(Long id) {
        // 检查当前用户权限
        User currentUser = getCurrentUser();
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE));

        if (!hasPermission(currentUser, targetUser)) {
            throw new RuntimeException("没有权限删除该用户");
        }

        // 教员不能被删除
        if (targetUser.getUserType() == UserType.TEACHER) {
            throw new RuntimeException("教员账号不能被删除");
        }

        userRepository.delete(targetUser);
    }

    /**
     * 切换用户启用状态
     * 如果用户当前是启用状态，则禁用；如果是禁用状态，则启用
     * @param id 用户ID
     * @throws RuntimeException 当用户不存在时抛出
     */
    public void toggleUserStatus(Long id) {
        // 检查当前用户权限
        User currentUser = getCurrentUser();
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE));

        if (!hasPermission(currentUser, targetUser)) {
            throw new RuntimeException("没有权限修改该用户状态");
        }

        // 教员账号不能被禁用
        if (targetUser.getUserType() == UserType.TEACHER && targetUser.getEnabled()) {
            throw new RuntimeException("教员账号不能被禁用");
        }

        targetUser.setEnabled(!targetUser.getEnabled());
        userRepository.save(targetUser);
    }

    /**
     * 将User实体对象转换为UserDTO对象
     * 
     * @param user 用户实体对象
     * @return 转换后的UserDTO对象
     * @param interests 用户的兴趣列表（已预加载）
     */
     public UserDTO convertToDTO(User user, List<InterestType> interests) {
         UserDTO dto = new UserDTO();
         BeanUtils.copyProperties(user, dto, "password");

         // 转换生日日期为字符串格式
         if (user.getBirthday() != null) {
             DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
             dto.setBirthday(user.getBirthday().format(formatter));
         }

         // 转换创建时间为字符串格式
         if (user.getCreateTime() != null) {
             DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
             dto.setCreateTime(user.getCreateTime().format(formatter));
         }

         // 转换最后登录时间为字符串格式
         if (user.getLastLoginTime() != null) {
             DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
             dto.setLastLoginTime(user.getLastLoginTime().format(formatter));
         }

         // 转换更新时间为字符串格式
         if (user.getUpdateTime() != null) {
             DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
             dto.setUpdateTime(user.getUpdateTime().format(formatter));
         }

         // 设置用户兴趣 (使用传入的已预加载的兴趣列表)
         dto.setInterests(interests);

         return dto;
     }

    /**
     * 将User实体对象转换为UserDTO对象 (不加载兴趣)
     *
     * @param user 用户实体对象
     * @return 转换后的UserDTO对象
     */
    public UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto, "password");

        // 转换日期为字符串格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (user.getBirthday() != null) {
            dto.setBirthday(user.getBirthday().format(formatter));
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (user.getCreateTime() != null) {
            dto.setCreateTime(user.getCreateTime().format(dateTimeFormatter));
        }
        if (user.getLastLoginTime() != null) {
            dto.setLastLoginTime(user.getLastLoginTime().format(dateTimeFormatter));
        }
        if (user.getUpdateTime() != null) {
            dto.setUpdateTime(user.getUpdateTime().format(dateTimeFormatter));
        }

        // 不加载用户兴趣

        return dto;
    }

    /**
     * 用户登录
     * @param loginDTO 登录信息对象，包含用户名和密码
     * @return 包含token和用户类型的Map对象
     * @throws RuntimeException 当用户名或密码错误，或账号被禁用时抛出
     */
    public Map<String, Object> login(LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if (!user.getEnabled()) {
            throw new RuntimeException("账号已被禁用");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        // 生成包含用户ID的token
        String token = jwtUtil.generateToken(user.getUsername(), user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userType", user.getUserType());
        return result;
    }

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户DTO对象
     * @throws RuntimeException 当用户不存在时抛出
     */
    public UserDTO getUserInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));
        UserDTO userDTO = convertToDTO(user);
        
        // 如果是企业用户，添加公司信息
        if (UserType.ENTERPRISE.equals(user.getUserType()) && user.getCompany() != null) {
            Company company = user.getCompany();
            userDTO.setCompanyId(company.getId());
            userDTO.setCompanyName(company.getName());
            userDTO.setCompanyDetails(new CompanyDetails());
            userDTO.getCompanyDetails().setIndustry(company.getIndustry());
            userDTO.getCompanyDetails().setAddress(company.getAddress());
            userDTO.getCompanyDetails().setWebsite(company.getWebsite());
        }
        
        // 获取用户兴趣
        List<UserInterest> userInterests = userInterestRepository.findByUser(user);
        if (userInterests != null && !userInterests.isEmpty()) {
            List<InterestType> interests = userInterests.stream()
                    .map(UserInterest::getInterestType)
                    .collect(Collectors.toList());
            userDTO.setInterests(interests);
        }
        
        return userDTO;
    }

    /**
     * 获取当前登录用户
     * @return 当前登录用户
     * @throws UserNotLoggedInException 当用户未登录时抛出
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new UserNotLoggedInException("用户未登录");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotLoggedInException("用户未登录"));
    }

    /**
     * 根据用户ID获取用户信息
     * @param id 用户ID
     * @return 用户实体对象
     * @throws RuntimeException 当用户不存在时抛出
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE));
    }

    /**
     * 根据用户ID获取用户DTO信息
     * @param id 用户ID
     * @return 用户DTO对象
     * @throws RuntimeException 当用户不存在时抛出
     */
    public UserDTO getUserDTOById(Long id) {
        User user = getUserById(id);
        return convertToDTO(user);
    }

    /**
     * 更新用户个性签名
     * @param introduction 新的个性签名
     * @throws RuntimeException 当用户未登录时抛出
     */
    public void updateIntroduction(String introduction) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        currentUser.setIntroduction(introduction);
        currentUser.setUpdateTime(LocalDateTime.now());
        userRepository.save(currentUser);
    }

    /**
     * 根据公司ID查找企业用户
     * 
     * @param companyId 公司ID
     * @return 该公司的所有企业用户列表
     */
    public List<User> findUsersByCompanyId(Long companyId) {
        if (companyId == null) {
            throw new IllegalArgumentException("公司ID不能为空");
        }
        // 验证公司是否存在
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("找不到ID为 " + companyId + " 的公司");
        }
        return userRepository.findByCompanyId(companyId);
    }

    /**
     * 根据公司ID查找企业用户(分页)
     * 
     * @param companyId 公司ID
     * @param pageable 分页参数
     * @return 分页后的企业用户列表
     */
    public Page<User> findUsersByCompanyId(Long companyId, Pageable pageable) {
        if (companyId == null) {
            throw new IllegalArgumentException("公司ID不能为空");
        }
        // 验证公司是否存在
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("找不到ID为 " + companyId + " 的公司");
        }
        return userRepository.findByCompanyId(companyId, pageable);
    }

    /**
     * 检查指定ID的用户是否存在
     * @param id 用户ID
     * @return 用户是否存在
     */
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * 根据经验值计算用户等级，使用与APP端一致的计算方式
     * @param experience 用户经验值
     * @return 用户等级 (1-5)
     */
    public int calculateUserLevel(int experience) {
        if (experience < LEVEL_1_TO_2_EXP) {
            return 1;
        } else if (experience < LEVEL_1_TO_2_EXP + LEVEL_2_TO_3_EXP) {
            return 2;
        } else if (experience < LEVEL_1_TO_2_EXP + LEVEL_2_TO_3_EXP + LEVEL_3_TO_4_EXP) {
            return 3;
        } else if (experience < LEVEL_1_TO_2_EXP + LEVEL_2_TO_3_EXP + LEVEL_3_TO_4_EXP + LEVEL_4_TO_5_EXP) {
            return 4;
        } else {
            return 5;
        }
    }

    /**
     * 获取升级到下一等级所需的总经验值
     * @param currentLevel 当前等级
     * @return 下一等级所需的总经验值，如果已经是最高等级则返回-1
     */
    public int getExperienceForNextLevel(int currentLevel) {
        switch (currentLevel) {
            case 1:
                return LEVEL_1_TO_2_EXP;
            case 2:
                return LEVEL_1_TO_2_EXP + LEVEL_2_TO_3_EXP;
            case 3:
                return LEVEL_1_TO_2_EXP + LEVEL_2_TO_3_EXP + LEVEL_3_TO_4_EXP;
            case 4:
                return LEVEL_1_TO_2_EXP + LEVEL_2_TO_3_EXP + LEVEL_3_TO_4_EXP + LEVEL_4_TO_5_EXP;
            case 5:
                return -1; // 已经是最高等级
            default:
                return LEVEL_1_TO_2_EXP; // 默认返回一级到二级的经验值
        }
    }

    /**
     * 获取当前等级的起始经验值
     * @param level 用户等级
     * @return 该等级的起始经验值
     */
    public int getBaseExperienceForLevel(int level) {
        switch (level) {
            case 1:
                return 0;
            case 2:
                return LEVEL_1_TO_2_EXP;
            case 3:
                return LEVEL_1_TO_2_EXP + LEVEL_2_TO_3_EXP;
            case 4:
                return LEVEL_1_TO_2_EXP + LEVEL_2_TO_3_EXP + LEVEL_3_TO_4_EXP;
            case 5:
                return LEVEL_1_TO_2_EXP + LEVEL_2_TO_3_EXP + LEVEL_3_TO_4_EXP + LEVEL_4_TO_5_EXP;
            default:
                return 0;
        }
    }

    /**
     * 通过用户名模糊搜索用户
     * @param username 用户名
     * @return 匹配的用户DTO列表
     */
    public List<UserDTO> searchByUsername(String username) {
        List<User> users = userRepository.findByUsernameContaining(username);
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * 通过用户名模糊或ID精确搜索用户
     * @param keyword 用户名或ID
     * @return 匹配的用户DTO列表
     */
    public List<UserDTO> searchByKeyword(String keyword) {
        List<User> users = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(); // 输入为空，返回空列表
        }

        // 1. 用户名模糊搜索（包含数字用户名）
        List<User> nameUsers = userRepository.findByUsernameContaining(keyword);
        users.addAll(nameUsers);

        // 2. 如果输入符合手机号格式，尝试按手机号精确搜索并去重
        if (keyword.matches("^1[3-9]\\d{9}$")) {
            Optional<User> phoneUserOptional = userRepository.findByPhone(keyword);
            if (phoneUserOptional.isPresent()) {
                User phoneUser = phoneUserOptional.get();
                // 检查是否已包含在用户名搜索结果中，避免重复添加
                if (users.stream().noneMatch(u -> u.getId().equals(phoneUser.getId()))) {
                    users.add(phoneUser);
                }
            }
        }

        // 转换并返回DTO列表
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
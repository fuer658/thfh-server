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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
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

/**
 * 用户服务类
 * 提供用户相关的业务逻辑处理，包括用户的创建、查询、修改、删除等操作
 * 以及用户登录、状态管理等功能
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserInterestRepository userInterestRepository;

    @Autowired
    private CompanyRepository companyRepository;

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

        return userPage.map(this::convertToDTO);
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
                .orElseThrow(() -> new RuntimeException("用户不存在"));

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
                .orElseThrow(() -> new RuntimeException("用户不存在"));

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
                .orElseThrow(() -> new RuntimeException("用户不存在"));

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
     */
    public UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);

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
        
        // 获取用户兴趣
        List<UserInterest> userInterests = userInterestRepository.findByUser(user);
        if (userInterests != null && !userInterests.isEmpty()) {
            List<InterestType> interests = userInterests.stream()
                    .map(UserInterest::getInterestType)
                    .collect(Collectors.toList());
            dto.setInterests(interests);
        }

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
                .orElseThrow(() -> new RuntimeException("用户不存在"));
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
     * 获取当前登录用户信息
     * @return 当前登录用户实体对象，如果未登录则返回null
     * @throws RuntimeException 当用户不存在时抛出
     */
    public User getCurrentUser() {
        org.springframework.security.core.Authentication authentication =
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    /**
     * 根据用户ID获取用户信息
     * @param id 用户ID
     * @return 用户实体对象
     * @throws RuntimeException 当用户不存在时抛出
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
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
}
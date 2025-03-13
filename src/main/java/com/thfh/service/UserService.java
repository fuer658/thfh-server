package com.thfh.service;

import com.thfh.dto.UserDTO;
import com.thfh.dto.UserQueryDTO;
import com.thfh.dto.LoginDTO;
import com.thfh.model.User;
import com.thfh.repository.UserRepository;
import com.thfh.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
     * 创建新用户
     * @param userDTO 用户信息对象，包含用户的基本信息
     * @return 创建成功的用户DTO对象
     * @throws RuntimeException 当用户名已存在时抛出
     */
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        BeanUtils.copyProperties(userDTO, user, "birthday");
        
        // 处理生日日期
        if (userDTO.getBirthday() != null && !userDTO.getBirthday().isEmpty()) {
            user.setBirthday(LocalDate.parse(userDTO.getBirthday()));
        }
        
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        BeanUtils.copyProperties(userDTO, user, "id", "password", "createTime", "updateTime", "birthday");
        
        // 处理生日日期
        if (userDTO.getBirthday() != null && !userDTO.getBirthday().isEmpty()) {
            user.setBirthday(LocalDate.parse(userDTO.getBirthday()));
        }
        
        user.setUpdateTime(LocalDateTime.now());
        user = userRepository.save(user);

        return convertToDTO(user);
    }

    /**
     * 删除指定ID的用户
     * @param id 要删除的用户ID
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * 切换用户启用状态
     * 如果用户当前是启用状态，则禁用；如果是禁用状态，则启用
     * @param id 用户ID
     * @throws RuntimeException 当用户不存在时抛出
     */
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setEnabled(!user.getEnabled());
        userRepository.save(user);
    }

    /**
     * 将用户实体对象转换为DTO对象
     * 处理日期和时间格式转换
     * @param user 用户实体对象
     * @return 转换后的用户DTO对象
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        
        // 转换生日日期为字符串格式
        if (user.getBirthday() != null) {
            dto.setBirthday(user.getBirthday().toString()); // LocalDate默认格式为yyyy-MM-dd
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

        // 生成token
        String token = jwtUtil.generateToken(user.getUsername());

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
        return convertToDTO(user);
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
}
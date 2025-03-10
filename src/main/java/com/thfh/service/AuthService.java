package com.thfh.service;

import com.thfh.dto.AdminDTO;
import com.thfh.dto.LoginDTO;
import com.thfh.dto.UserDTO;
import com.thfh.model.Admin;
import com.thfh.model.User;
import com.thfh.repository.AdminRepository;
import com.thfh.repository.UserRepository;
import com.thfh.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务类
 * 提供用户认证相关的业务逻辑处理，包括管理员和普通用户的登录认证
 * 以及获取用户信息等功能
 */
@Service
public class AuthService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     * 先尝试管理员登录，如果失败则尝试普通用户登录
     * @param loginDTO 登录信息对象，包含用户名和密码
     * @return 包含token和用户类型的Map对象
     * @throws RuntimeException 当用户名或密码错误，或账号被禁用时抛出
     */
    public Map<String, Object> login(LoginDTO loginDTO) {
        // 先尝试管理员登录
        try {
            Admin admin = adminRepository.findByUsername(loginDTO.getUsername())
                    .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

            if (!admin.getEnabled()) {
                throw new RuntimeException("账号已被禁用");
            }

            if (!passwordEncoder.matches(loginDTO.getPassword(), admin.getPassword())) {
                throw new RuntimeException("用户名或密码错误");
            }

            // 更新最后登录时间
            admin.setLastLoginTime(LocalDateTime.now());
            adminRepository.save(admin);

            // 生成token
            String token = jwtUtil.generateToken(admin.getUsername());

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userType", "admin");
            return result;
        } catch (RuntimeException e) {
            // 管理员登录失败，尝试用户登录
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
    }

    /**
     * 获取用户信息
     * 先尝试获取管理员信息，如果不存在则尝试获取普通用户信息
     * @param username 用户名
     * @return 用户信息DTO对象
     * @throws RuntimeException 当用户不存在时抛出
     */
    public AdminDTO getUserInfo(String username) {
        try {
            Admin admin = adminRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            AdminDTO adminDTO = new AdminDTO();
            BeanUtils.copyProperties(admin, adminDTO);
            return adminDTO;
        } catch (RuntimeException e) {
            // 如果管理员不存在，尝试查找用户
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            AdminDTO adminDTO = new AdminDTO();
            BeanUtils.copyProperties(user, adminDTO);
            return adminDTO;
        }
    }

    /**
     * 获取用户个人资料
     * @param username 用户名
     * @return 用户DTO对象
     * @throws RuntimeException 当用户不存在时抛出
     */
    public UserDTO getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }
}
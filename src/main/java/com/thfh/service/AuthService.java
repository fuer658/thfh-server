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
            // 如果不是管理员，尝试普通用户登录
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
            result.put("userType", user.getUserType().name());
            return result;
        }
    }

    public Object getUserInfo(String username) {
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

            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            return userDTO;
        }
    }

    public UserDTO getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }
}
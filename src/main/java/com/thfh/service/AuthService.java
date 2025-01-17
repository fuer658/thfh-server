package com.thfh.service;

import com.thfh.dto.AdminDTO;
import com.thfh.dto.LoginDTO;
import com.thfh.model.Admin;
import com.thfh.repository.AdminRepository;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> login(LoginDTO loginDTO) {
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
        return result;
    }

    public AdminDTO getUserInfo(String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        AdminDTO adminDTO = new AdminDTO();
        BeanUtils.copyProperties(admin, adminDTO);
        return adminDTO;
    }
} 
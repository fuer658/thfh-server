package com.thfh.service;

import com.thfh.dto.AdminDTO;
import com.thfh.dto.LoginDTO;
import com.thfh.dto.UserDTO;
import com.thfh.model.Admin;
import com.thfh.model.Company;
import com.thfh.model.User;
import com.thfh.model.UserType;
import com.thfh.repository.AdminRepository;
import com.thfh.repository.CompanyRepository;
import com.thfh.repository.UserRepository;
import com.thfh.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> login(LoginDTO loginDTO, javax.servlet.http.HttpServletRequest request) {
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

            // 更新最后登录时间和最近登录IP
            user.setLastLoginTime(LocalDateTime.now());
            if (request != null) {
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                } else {
                    // X-Forwarded-For 可能有多个IP，取第一个
                    ip = ip.split(",")[0].trim();
                }
                user.setRecentLoginIp(ip);
            }
            userRepository.save(user);

            // 生成包含用户ID的token
            String token = jwtUtil.generateToken(user.getUsername(), user.getId());

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userType", user.getUserType().name());
            return result;
        }
    }
    
    /**
     * 用户注册
     * @param userDTO 用户信息
     * @return 注册成功的用户信息
     * @throws RuntimeException 当用户名已存在或者企业用户未提供公司信息时抛出
     */
    @Transactional
    public UserDTO register(UserDTO userDTO) {
        // 验证用户名是否已存在
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 验证企业用户必须提供公司信息
        if (UserType.ENTERPRISE.equals(userDTO.getUserType())) {
            if ((userDTO.getCompanyId() == null || userDTO.getCompanyId() <= 0) 
                && (userDTO.getCompanyName() == null || userDTO.getCompanyName().isEmpty())) {
                throw new RuntimeException("企业用户必须提供公司信息");
            }
        }
        
        // 创建用户对象
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        
        // 设置默认值
        user.setEnabled(true);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 企业用户处理
        if (UserType.ENTERPRISE.equals(user.getUserType())) {
            Company company = null;
            
            // 尝试根据ID查找公司
            if (userDTO.getCompanyId() != null && userDTO.getCompanyId() > 0) {
                company = companyRepository.findById(userDTO.getCompanyId())
                        .orElse(null);
            }
            
            // 如果ID查找失败，尝试根据名称查找
            if (company == null && userDTO.getCompanyName() != null && !userDTO.getCompanyName().isEmpty()) {
                company = companyRepository.findByName(userDTO.getCompanyName());
                
                // 如果找不到公司，则创建新公司
                if (company == null) {
                    company = new Company();
                    company.setName(userDTO.getCompanyName());
                    
                    // 设置额外的公司信息（如果有）
                    if (userDTO.getCompanyDetails() != null) {
                        // 设置公司行业
                        if (userDTO.getCompanyDetails().getIndustry() != null) {
                            company.setIndustry(userDTO.getCompanyDetails().getIndustry());
                        }
                        
                        // 设置公司地址
                        if (userDTO.getCompanyDetails().getAddress() != null) {
                            company.setAddress(userDTO.getCompanyDetails().getAddress());
                        }
                        
                        // 设置公司网站（选填）
                        if (userDTO.getCompanyDetails().getWebsite() != null) {
                            company.setWebsite(userDTO.getCompanyDetails().getWebsite());
                        }
                    }
                    
                    company.setEnabled(true);
                    company.setCreateTime(LocalDateTime.now());
                    company.setUpdateTime(LocalDateTime.now());
                    company = companyRepository.save(company);
                }
            }
            
            // 关联公司
            user.setCompany(company);
        }
        
        // 保存用户
        user = userRepository.save(user);
        
        // 构建返回对象
        UserDTO result = new UserDTO();
        BeanUtils.copyProperties(user, result);
        
        // 设置公司相关信息
        if (user.getCompany() != null) {
            result.setCompanyId(user.getCompany().getId());
            result.setCompanyName(user.getCompany().getName());
        }
        
        return result;
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
            
            // 企业用户，添加公司信息
            if (UserType.ENTERPRISE.equals(user.getUserType()) && user.getCompany() != null) {
                userDTO.setCompanyId(user.getCompany().getId());
                userDTO.setCompanyName(user.getCompany().getName());
            }
            
            return userDTO;
        }
    }

    public UserDTO getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        
        // 企业用户，添加公司信息
        if (UserType.ENTERPRISE.equals(user.getUserType()) && user.getCompany() != null) {
            userDTO.setCompanyId(user.getCompany().getId());
            userDTO.setCompanyName(user.getCompany().getName());
        }
        
        return userDTO;
    }

    /**
     * 刷新JWT令牌
     * 
     * @param token 当前JWT令牌
     * @return 包含新令牌的结果Map
     * @throws RuntimeException 令牌验证失败时抛出
     */
    public Map<String, Object> refreshToken(String token) {
        try {
            // 验证令牌是否有效
            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("无效的令牌");
            }
            
            // 获取用户名
            String username = jwtUtil.getUsernameFromToken(token);
            
            // 获取用户ID（如果存在）
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            // 生成新令牌
            String newToken;
            if (userId != null) {
                newToken = jwtUtil.generateToken(username, userId);
            } else {
                newToken = jwtUtil.generateToken(username);
            }
            
            // 尝试获取用户或管理员类型
            String userType = null;
            try {
                Admin admin = adminRepository.findByUsername(username).orElse(null);
                if (admin != null) {
                    userType = "admin";
                } else {
                    User user = userRepository.findByUsername(username).orElse(null);
                    if (user != null) {
                        userType = user.getUserType().name();
                    }
                }
            } catch (Exception e) {
                // 忽略异常，userType将保持为null
            }
            
            // 返回新令牌
            Map<String, Object> result = new HashMap<>();
            result.put("token", newToken);
            if (userType != null) {
                result.put("userType", userType);
            }
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("刷新令牌失败: " + e.getMessage());
        }
    }

    /**
     * 校验JWT Token是否有效
     * @param token 前端传递的JWT Token
     * @return 有效返回true，无效返回false
     */
    public boolean verifyJwtToken(String token) {
        return jwtUtil.validateToken(token);
    }
}
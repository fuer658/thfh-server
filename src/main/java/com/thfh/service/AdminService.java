package com.thfh.service;

import com.thfh.dto.AdminDTO;
import com.thfh.model.Admin;
import com.thfh.repository.AdminRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 管理员服务实现类
 * 定义管理员相关的业务逻辑操作，包括管理员的创建、查询、修改、删除等功能
 * 以及管理员状态管理等功能
 */
@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取管理员列表
     * @param queryDTO 查询条件对象，包含用户名、真实姓名、启用状态等过滤条件
     * @return 分页后的管理员DTO列表
     */
    public Page<AdminDTO> getAdmins(AdminDTO queryDTO) {
        Specification<Admin> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (queryDTO.getUsername() != null && !queryDTO.getUsername().isEmpty()) {
                predicates.add(cb.like(root.get("username"), "%" + queryDTO.getUsername() + "%"));
            }
            
            if (queryDTO.getEnabled() != null) {
                predicates.add(cb.equal(root.get("enabled"), queryDTO.getEnabled()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Admin> adminPage = adminRepository.findAll(spec, Pageable.unpaged());
        List<AdminDTO> adminDTOs = adminPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(adminDTOs, adminPage.getPageable(), adminPage.getTotalElements());
    }

    /**
     * 创建管理员
     * @param adminDTO 管理员信息对象，包含管理员的基本信息
     * @return 创建成功的管理员DTO对象
     * @throws RuntimeException 当用户名已存在时抛出
     */
    @Transactional
    public AdminDTO createAdmin(AdminDTO adminDTO) {
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminDTO, admin);
        admin.setEnabled(true);
        admin.setPassword(passwordEncoder.encode(adminDTO.getPassword())); // 加密密码
        
        Admin savedAdmin = adminRepository.save(admin);
        return convertToDTO(savedAdmin);
    }

    /**
     * 更新管理员信息
     * @param id 管理员ID
     * @param adminDTO 更新后的管理员信息对象
     * @return 更新后的管理员DTO对象
     * @throws RuntimeException 当管理员不存在时抛出
     */
    @Transactional
    public AdminDTO updateAdmin(Long id, AdminDTO adminDTO) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (!optionalAdmin.isPresent()) {
            throw new RuntimeException("管理员不存在");
        }
        
        Admin admin = optionalAdmin.get();
        BeanUtils.copyProperties(adminDTO, admin, "id", "password");
        
        if (adminDTO.getPassword() != null && !adminDTO.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(adminDTO.getPassword())); // 加密新密码
        }
        
        Admin updatedAdmin = adminRepository.save(admin);
        return convertToDTO(updatedAdmin);
    }

    /**
     * 删除管理员
     * @param id 要删除的管理员ID
     */
    @Transactional
    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new RuntimeException("管理员不存在");
        }
        adminRepository.deleteById(id);
    }

    /**
     * 切换管理员启用状态
     * 如果管理员当前是启用状态，则禁用；如果是禁用状态，则启用
     * @param id 管理员ID
     * @throws RuntimeException 当管理员不存在时抛出
     */
    @Transactional
    public void toggleAdminStatus(Long id) {
        Optional<Admin> optionalAdmin = adminRepository.findById(id);
        if (!optionalAdmin.isPresent()) {
            throw new RuntimeException("管理员不存在");
        }
        
        Admin admin = optionalAdmin.get();
        admin.setEnabled(!admin.getEnabled());
        adminRepository.save(admin);
    }

    /**
     * 检查用户是否为管理员
     * @param username 用户名
     * @return 如果是管理员返回true，否则返回false
     */
    public boolean isAdmin(String username) {
        return adminRepository.findByUsername(username)
                .map(Admin::getEnabled)
                .orElse(false);
    }

    private AdminDTO convertToDTO(Admin admin) {
        AdminDTO dto = new AdminDTO();
        BeanUtils.copyProperties(admin, dto);
        return dto;
    }
}
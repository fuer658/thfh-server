package com.thfh.service.impl;

import com.thfh.dto.AdminDTO;
import com.thfh.model.Admin;
import com.thfh.repository.AdminRepository;
import com.thfh.service.AdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // 添加 PasswordEncoder

    @Override
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

    @Override
    @Transactional
    public AdminDTO createAdmin(AdminDTO adminDTO) {
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminDTO, admin);
        admin.setEnabled(true);
        admin.setPassword(passwordEncoder.encode(adminDTO.getPassword())); // 加密密码
        
        Admin savedAdmin = adminRepository.save(admin);
        return convertToDTO(savedAdmin);
    }

    @Override
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

    @Override
    @Transactional
    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new RuntimeException("管理员不存在");
        }
        adminRepository.deleteById(id);
    }

    @Override
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

    @Override
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
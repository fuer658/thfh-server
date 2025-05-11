package com.thfh.service;

import com.thfh.dto.DisabilityCertificationDTO;
import com.thfh.model.DisabilityCertification;
import com.thfh.model.User;
import com.thfh.repository.DisabilityCertificationRepository;
import com.thfh.repository.UserRepository;
import com.thfh.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DisabilityCertificationService {

    @Autowired
    private DisabilityCertificationRepository certificationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 提交残疾人认证申请
     */
    @Transactional
    public DisabilityCertificationDTO submitCertification(Long userId, DisabilityCertificationDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 检查是否已有待审核或已通过的认证
        if (certificationRepository.existsByUserAndStatus(user, DisabilityCertification.Status.PENDING)) {
            throw new BusinessException("您已有待审核的认证申请，请等待审核结果");
        }
        
        if (certificationRepository.existsByUserAndStatus(user, DisabilityCertification.Status.APPROVED)) {
            throw new BusinessException("您已通过认证，无需重复认证");
        }

        DisabilityCertification certification = new DisabilityCertification();
        BeanUtils.copyProperties(request, certification);
        certification.setUser(user);
        certification.setStatus(DisabilityCertification.Status.PENDING);

        certification = certificationRepository.save(certification);
        return convertToDTO(certification);
    }

    /**
     * 获取用户的认证详情
     */
    public DisabilityCertificationDTO getUserCertification(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        Optional<DisabilityCertification> certificationOpt = 
                certificationRepository.findTopByUserOrderByCreateTimeDesc(user);
        
        return certificationOpt.map(this::convertToDTO)
                .orElseThrow(() -> new BusinessException("未找到认证记录"));
    }

    /**
     * 审核认证申请
     */
    @Transactional
    public DisabilityCertificationDTO reviewCertification(Long certificationId, DisabilityCertification.Status status, String rejectReason) {
        DisabilityCertification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new BusinessException("认证记录不存在"));

        if (certification.getStatus() != DisabilityCertification.Status.PENDING) {
            throw new BusinessException("只能审核待审核状态的申请");
        }

        certification.setStatus(status);
        
        if (status == DisabilityCertification.Status.REJECTED) {
            if (rejectReason == null || rejectReason.trim().isEmpty()) {
                throw new BusinessException("拒绝认证时必须提供拒绝原因");
            }
            certification.setRejectReason(rejectReason);
        } else if (status == DisabilityCertification.Status.APPROVED) {
            // 更新用户的残疾类型信息
            User user = certification.getUser();
            user.setDisability(certification.getDisabilityType());
            userRepository.save(user);
        }

        certification = certificationRepository.save(certification);
        return convertToDTO(certification);
    }

    /**
     * 分页查询认证列表
     */
    public Page<DisabilityCertificationDTO> getCertifications(Specification<DisabilityCertification> spec, Pageable pageable) {
        return certificationRepository.findAll(spec, pageable)
                .map(this::convertToDTO);
    }

    /**
     * 根据状态查询认证列表
     */
    public List<DisabilityCertificationDTO> getCertificationsByStatus(DisabilityCertification.Status status) {
        return certificationRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将实体转换为DTO
     */
    private DisabilityCertificationDTO convertToDTO(DisabilityCertification certification) {
        DisabilityCertificationDTO dto = new DisabilityCertificationDTO();
        BeanUtils.copyProperties(certification, dto);
        
        User user = certification.getUser();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        
        if (certification.getStatus() != null) {
            dto.setStatusDescription(certification.getStatus().getDescription());
        }
        
        return dto;
    }
} 
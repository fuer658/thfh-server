package com.thfh.service;

import com.thfh.dto.PointsRecordDTO;
import com.thfh.dto.PointsAdjustDTO;
import com.thfh.dto.PointsQueryDTO;
import com.thfh.model.PointsRecord;
import com.thfh.model.PointsType;
import com.thfh.model.User;
import com.thfh.repository.PointsRecordRepository;
import com.thfh.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PointsService {
    @Autowired
    private PointsRecordRepository pointsRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public Page<PointsRecordDTO> getPointsRecords(PointsQueryDTO queryDTO) {
        Specification<PointsRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (queryDTO.getStudentId() != null) {
                predicates.add(cb.equal(root.get("student").get("id"), queryDTO.getStudentId()));
            }
            if (queryDTO.getType() != null) {
                predicates.add(cb.equal(root.get("type"), queryDTO.getType()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Page<PointsRecord> recordPage = pointsRecordRepository.findAll(spec, 
            PageRequest.of(queryDTO.getPageNum() - 1, queryDTO.getPageSize(), sort));
        
        return recordPage.map(this::convertToDTO);
    }

    @Transactional
    public PointsRecordDTO adjustPoints(PointsAdjustDTO adjustDTO) {
        User student = userRepository.findById(adjustDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("学员不存在"));

        // 创建积分记录
        PointsRecord record = new PointsRecord();
        record.setStudent(student);
        record.setPoints(adjustDTO.getPoints());
        record.setType(PointsType.ADMIN_ADJUST);
        record.setDescription(adjustDTO.getDescription());
        record = pointsRecordRepository.save(record);

        // 更新学员积分，如果当前积分为null则初始化为0
        Integer currentPoints = student.getPoints();
        if (currentPoints == null) {
            currentPoints = 0;
        }
        student.setPoints(currentPoints + adjustDTO.getPoints());
        
        // 如果需要同时调整经验值
        if (Boolean.TRUE.equals(adjustDTO.getIncludeExperience()) && adjustDTO.getExperienceAmount() != null) {
            Integer currentExperience = student.getExperience();
            if (currentExperience == null) {
                currentExperience = 0;
            }
            student.setExperience(currentExperience + adjustDTO.getExperienceAmount());
            
            // 使用UserService中的方法计算新等级
            int newExperience = student.getExperience();
            int newLevel = userService.calculateUserLevel(newExperience);
            student.setLevel(newLevel);
        }
        
        userRepository.save(student);

        return convertToDTO(record);
    }

    private PointsRecordDTO convertToDTO(PointsRecord record) {
        PointsRecordDTO dto = new PointsRecordDTO();
        BeanUtils.copyProperties(record, dto);
        dto.setStudentId(record.getStudent().getId());
        dto.setStudentName(record.getStudent().getRealName());
        return dto;
    }

    public Integer getCurrentUserPoints() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        return currentUser.getPoints();
    }
}
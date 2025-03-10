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

import javax.persistence.criteria.Predicate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 积分服务类
 * 提供积分相关的业务逻辑处理，包括积分记录的查询、积分调整等操作
 */
@Service
public class PointsService {
    @Autowired
    private PointsRecordRepository pointsRecordRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 根据查询条件获取积分记录列表
     * @param queryDTO 查询条件对象，包含学员ID、积分类型等过滤条件
     * @return 分页后的积分记录DTO列表
     */
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

    /**
     * 调整学员积分
     * 创建积分调整记录并更新学员总积分
     * @param adjustDTO 积分调整信息对象，包含学员ID、积分数量、描述等信息
     * @return 创建成功的积分记录DTO对象
     * @throws RuntimeException 当学员不存在时抛出
     */
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

        // 更新学员积分
        student.setPoints(student.getPoints() + adjustDTO.getPoints());
        userRepository.save(student);

        return convertToDTO(record);
    }

    /**
     * 将积分记录实体对象转换为DTO对象
     * @param record 积分记录实体对象
     * @return 转换后的积分记录DTO对象
     */
    private PointsRecordDTO convertToDTO(PointsRecord record) {
        PointsRecordDTO dto = new PointsRecordDTO();
        BeanUtils.copyProperties(record, dto);
        dto.setStudentId(record.getStudent().getId());
        dto.setStudentName(record.getStudent().getRealName());
        
        // 格式化创建时间
        if (record.getCreateTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            dto.setCreateTime(record.getCreateTime().format(formatter));
        }
        
        return dto;
    }
} 
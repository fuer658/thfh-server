package com.thfh.service;

import com.thfh.dto.WorkDTO;
import com.thfh.dto.WorkQueryDTO;
import com.thfh.model.Work;
import com.thfh.model.User;
import com.thfh.model.WorkStatus;
import com.thfh.repository.WorkRepository;
import com.thfh.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkService {
    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<WorkDTO> getWorks(WorkQueryDTO queryDTO) {
        Specification<Work> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (queryDTO.getTitle() != null) {
                predicates.add(cb.like(root.get("title"), "%" + queryDTO.getTitle() + "%"));
            }
            if (queryDTO.getStudentId() != null) {
                predicates.add(cb.equal(root.get("student").get("id"), queryDTO.getStudentId()));
            }
            if (queryDTO.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), queryDTO.getStatus()));
            }
            if (queryDTO.getEnabled() != null) {
                predicates.add(cb.equal(root.get("enabled"), queryDTO.getEnabled()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Work> workPage = workRepository.findAll(spec, 
            PageRequest.of(queryDTO.getPageNum() - 1, queryDTO.getPageSize()));
        
        return workPage.map(this::convertToDTO);
    }

    @Transactional
    public WorkDTO createWork(WorkDTO workDTO) {
        if (workRepository.existsByTitleAndStudentId(workDTO.getTitle(), workDTO.getStudentId())) {
            throw new RuntimeException("该学员已存在同名作品");
        }

        User student = userRepository.findById(workDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("学员不存在"));

        Work work = new Work();
        BeanUtils.copyProperties(workDTO, work);
        work.setStudent(student);
        work.setStatus(WorkStatus.PENDING); // 新创建的作品默认为待审核状态
        work = workRepository.save(work);
        
        return convertToDTO(work);
    }

    @Transactional
    public WorkDTO updateWork(Long id, WorkDTO workDTO) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("作品不存在"));

        BeanUtils.copyProperties(workDTO, work, "id", "student", "createTime");
        work = workRepository.save(work);
        
        return convertToDTO(work);
    }

    @Transactional
    public void approveWork(Long id) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("作品不存在"));
        
        if (work.getStatus() != WorkStatus.PENDING) {
            throw new RuntimeException("只能审核待审核状态的作品");
        }
        
        work.setStatus(WorkStatus.ON_SALE);
        workRepository.save(work);
    }

    @Transactional
    public void rejectWork(Long id) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("作品不存在"));
        
        if (work.getStatus() != WorkStatus.PENDING) {
            throw new RuntimeException("只能审核待审核状态的作品");
        }
        
        work.setStatus(WorkStatus.OFFLINE);
        workRepository.save(work);
    }

    public void deleteWork(Long id) {
        workRepository.deleteById(id);
    }

    public void toggleWorkStatus(Long id) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("作品不存在"));
        work.setEnabled(!work.getEnabled());
        workRepository.save(work);
    }

    private WorkDTO convertToDTO(Work work) {
        WorkDTO dto = new WorkDTO();
        BeanUtils.copyProperties(work, dto);
        dto.setStudentId(work.getStudent().getId());
        dto.setStudentName(work.getStudent().getRealName());
        return dto;
    }
} 
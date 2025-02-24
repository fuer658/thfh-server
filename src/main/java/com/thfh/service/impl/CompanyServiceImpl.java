package com.thfh.service.impl;

import com.thfh.dto.CompanyDTO;
import com.thfh.dto.CompanyQueryDTO;
import com.thfh.model.Company;
import com.thfh.repository.CompanyRepository;
import com.thfh.service.CompanyService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    private CompanyDTO toDTO(Company company) {
        if (company == null) {
            return null;
        }
        CompanyDTO dto = new CompanyDTO();
        BeanUtils.copyProperties(company, dto);
        return dto;
    }

    private Company toEntity(CompanyDTO dto) {
        if (dto == null) {
            return null;
        }
        Company company = new Company();
        BeanUtils.copyProperties(dto, company);
        return company;
    }

    @Override
    @Transactional
    public CompanyDTO create(CompanyDTO companyDTO) {
        Company company = toEntity(companyDTO);
        company.setCreateTime(LocalDateTime.now());
        company.setUpdateTime(LocalDateTime.now());
        company.setEnabled(true);
        return toDTO(companyRepository.save(company));
    }

    @Override
    @Transactional
    public CompanyDTO update(Long id, CompanyDTO companyDTO) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公司不存在"));
        BeanUtils.copyProperties(companyDTO, company, "id", "createTime", "enabled");
        company.setUpdateTime(LocalDateTime.now());
        return toDTO(companyRepository.save(company));
    }

    @Override
    public CompanyDTO findById(Long id) {
        return companyRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("公司不存在"));
    }

    @Override
    public Page<CompanyDTO> findByCondition(CompanyQueryDTO queryDTO) {
        Specification<Company> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (queryDTO.getName() != null) {
                predicates.add(cb.like(root.get("name"), "%" + queryDTO.getName() + "%"));
            }
            if (queryDTO.getIndustry() != null) {
                predicates.add(cb.equal(root.get("industry"), queryDTO.getIndustry()));
            }
            if (queryDTO.getEnabled() != null) {
                predicates.add(cb.equal(root.get("enabled"), queryDTO.getEnabled()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Company> page = companyRepository.findAll(spec, queryDTO.toPageable());
        List<CompanyDTO> dtos = page.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        companyRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Boolean enabled) {
        companyRepository.updateStatus(id, enabled);
    }
}

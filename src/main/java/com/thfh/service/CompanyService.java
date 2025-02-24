package com.thfh.service;

import com.thfh.dto.CompanyDTO;
import com.thfh.dto.CompanyQueryDTO;
import com.thfh.model.Company;
import org.springframework.data.domain.Page;

public interface CompanyService {
    CompanyDTO create(CompanyDTO companyDTO);
    
    CompanyDTO update(Long id, CompanyDTO companyDTO);
    
    CompanyDTO findById(Long id);
    
    Page<CompanyDTO> findByCondition(CompanyQueryDTO queryDTO);
    
    void deleteById(Long id);
    
    void updateStatus(Long id, Boolean enabled);
}

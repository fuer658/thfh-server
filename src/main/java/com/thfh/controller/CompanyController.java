package com.thfh.controller;

import com.thfh.common.R;
import com.thfh.model.Company;
import com.thfh.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    // 创建公司
    @PostMapping
    public R create(@RequestBody Company company) {
        company.setCreateTime(LocalDateTime.now());
        company.setUpdateTime(LocalDateTime.now());
        company.setEnabled(true);
        Company savedCompany = companyRepository.save(company);
        return R.ok().data(savedCompany);
    }

    // 获取公司列表(分页)
    @GetMapping
    public R list(Pageable pageable) {
        Page<Company> companies = companyRepository.findAll(pageable);
        return R.ok().data(companies);
    }

    // 获取单个公司详情
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        Optional<Company> company = companyRepository.findById(id);
        return company.isPresent() ? R.ok().data(company.get()) : R.error("公司不存在");
    }

    // 更新公司信息
    @PutMapping("/{id}")
    public R update(@PathVariable Long id, @RequestBody Company company) {
        Optional<Company> existingCompany = companyRepository.findById(id);
        if (existingCompany.isPresent()) {
            Company updatedCompany = existingCompany.get();
            updatedCompany.setName(company.getName());
            updatedCompany.setLogo(company.getLogo());
            updatedCompany.setDescription(company.getDescription());
            updatedCompany.setIndustry(company.getIndustry());
            updatedCompany.setScale(company.getScale());
            updatedCompany.setWebsite(company.getWebsite());
            updatedCompany.setAddress(company.getAddress());
            updatedCompany.setWorkStartTime(company.getWorkStartTime());
            updatedCompany.setWorkEndTime(company.getWorkEndTime());
            updatedCompany.setUpdateTime(LocalDateTime.now());
            return R.ok().data(companyRepository.save(updatedCompany));
        }
        return R.error("公司不存在");
    }

    // 删除公司(软删除)
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        Optional<Company> company = companyRepository.findById(id);
        if (company.isPresent()) {
            Company companyToDelete = company.get();
            companyToDelete.setEnabled(false);
            companyToDelete.setUpdateTime(LocalDateTime.now());
            companyRepository.save(companyToDelete);
            return R.ok();
        }
        return R.error("公司不存在");
    }

    // 启用/禁用公司
    @PutMapping("/{id}/status")
    public R toggleStatus(@PathVariable Long id, @RequestParam Boolean enabled) {
        Optional<Company> company = companyRepository.findById(id);
        if (company.isPresent()) {
            Company companyToUpdate = company.get();
            companyToUpdate.setEnabled(enabled);
            companyToUpdate.setUpdateTime(LocalDateTime.now());
            return R.ok().data(companyRepository.save(companyToUpdate));
        }
        return R.error("公司不存在");
    }
}
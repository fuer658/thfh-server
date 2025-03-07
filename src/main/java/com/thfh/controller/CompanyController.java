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

/**
 * 企业管理控制器
 * 提供企业的增删改查和状态管理等功能
 */
@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    /**
     * 创建新企业
     * @param company 企业信息
     * @return 创建的企业信息
     */
    @PostMapping
    public R create(@RequestBody Company company) {
        company.setCreateTime(LocalDateTime.now());
        company.setUpdateTime(LocalDateTime.now());
        company.setEnabled(true);
        Company savedCompany = companyRepository.save(company);
        return R.ok().data(savedCompany);
    }

    /**
     * 获取企业列表
     * @param pageable 分页信息
     * @return 企业分页列表
     */
    @GetMapping
    public R list(Pageable pageable) {
        Page<Company> companies = companyRepository.findAll(pageable);
        return R.ok().data(companies);
    }

    /**
     * 获取企业详情
     * @param id 企业ID
     * @return 企业详细信息
     */
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        Optional<Company> company = companyRepository.findById(id);
        return company.isPresent() ? R.ok().data(company.get()) : R.error("公司不存在");
    }

    /**
     * 更新企业信息
     * @param id 企业ID
     * @param company 更新的企业信息
     * @return 更新后的企业信息
     */
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

    /**
     * 删除企业（软删除）
     * @param id 企业ID
     * @return 操作结果
     */
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

    /**
     * 切换企业状态（启用/禁用）
     * @param id 企业ID
     * @param enabled 是否启用
     * @return 更新后的企业信息
     */
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
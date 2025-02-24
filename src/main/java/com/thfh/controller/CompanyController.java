package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.CompanyDTO;
import com.thfh.dto.CompanyQueryDTO;
import com.thfh.service.CompanyService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public Result<CompanyDTO> create(@Valid @RequestBody CompanyDTO companyDTO) {
        return Result.success(companyService.create(companyDTO));
    }

    @PutMapping("/{id}")
    public Result<CompanyDTO> update(@PathVariable Long id, @Valid @RequestBody CompanyDTO companyDTO) {
        return Result.success(companyService.update(id, companyDTO));
    }

    @GetMapping("/{id}")
    public Result<CompanyDTO> findById(@PathVariable Long id) {
        return Result.success(companyService.findById(id));
    }

    @GetMapping
    public Result<Page<CompanyDTO>> findByCondition(CompanyQueryDTO queryDTO) {
        return Result.success(companyService.findByCondition(queryDTO));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        companyService.deleteById(id);
        return Result.success(null);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Boolean enabled) {
        companyService.updateStatus(id, enabled);
        return Result.success(null);
    }
}

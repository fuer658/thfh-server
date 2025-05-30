package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.model.Company;
import com.thfh.repository.CompanyRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 企业管理控制器
 * 提供企业的增删改查和状态管理等功能
 */
@Tag(name = "企业管理", description = "企业相关的API接口，包括企业的增删改查和状态管理等功能")
@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 创建新企业
     * @param company 企业信息
     * @return 创建的企业信息
     */
    @Operation(summary = "创建新企业", description = "创建一个新的企业，需要提供企业的基本信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @PostMapping
    public Result<Company> create(
            @Parameter(description = "企业信息", required = true) @RequestBody Company company) {
        company.setCreateTime(LocalDateTime.now());
        company.setUpdateTime(LocalDateTime.now());
        company.setEnabled(true);
        Company savedCompany = companyRepository.save(company);
        return Result.success(savedCompany);
    }

    /**
     * 获取企业列表
     * @param pageable 分页信息
     * @param name 公司名称(可选)
     * @param enabled 状态(可选)
     * @param tags 标签(可选)
     * @return 企业分页列表
     */
    @Operation(summary = "获取企业列表", description = "根据条件获取企业分页列表，支持按名称、状态和标签筛选")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping
    public Result<Page<Company>> list(
            @Parameter(description = "分页信息") Pageable pageable,
            @Parameter(description = "公司名称(可选)") @RequestParam(required = false) String name,
            @Parameter(description = "状态(可选)") @RequestParam(required = false) Boolean enabled,
            @Parameter(description = "标签(可选)") @RequestParam(required = false) String tags) {
        if (tags != null && !tags.trim().isEmpty()) {
            Page<Company> companies = companyRepository.findByCondition(
                name != null && !name.trim().isEmpty() ? name : null,
                enabled,
                tags,
                pageable
            );
            return Result.success(companies);
        } else {
            Page<Company> companies = companyRepository.findByCondition(
                name != null && !name.trim().isEmpty() ? name : null,
                enabled,
                pageable
            );
            return Result.success(companies);
        }
    }

    /**
     * 获取企业详情
     * @param id 企业ID
     * @return 企业详细信息
     */
    @Operation(summary = "获取企业详情", description = "通过企业ID查询企业的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "企业不存在")
    })
    @GetMapping("/{id}")
    public Result<Company> getById(
            @Parameter(description = "企业ID", required = true) @PathVariable Long id) {
        Optional<Company> company = companyRepository.findById(id);
        return company.isPresent() ? Result.success(company.get()) : Result.error("公司不存在");
    }

    /**
     * 更新企业信息
     * @param id 企业ID
     * @param company 更新的企业信息
     * @return 更新后的企业信息
     */
    @Operation(summary = "更新企业信息", description = "根据企业ID更新企业信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "企业不存在")
    })
    @PutMapping("/{id}")
    public Result<Company> update(
            @Parameter(description = "企业ID", required = true) @PathVariable Long id,
            @Parameter(description = "更新的企业信息", required = true) @RequestBody Company company) {
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
            updatedCompany.setTags(company.getTags()); // 保留标签字段
            updatedCompany.setUpdateTime(LocalDateTime.now());
            return Result.success(companyRepository.save(updatedCompany));
        }
        return Result.error("公司不存在");
    }

    /**
     * 删除企业
     * @param id 企业ID
     * @return 操作结果
     */
    @Operation(summary = "删除企业", description = "根据企业ID删除企业，同时删除关联的职位")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "企业不存在"),
        @ApiResponse(responseCode = "500", description = "删除失败")
    })
    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> delete(
            @Parameter(description = "企业ID", required = true) @PathVariable Long id) {
        System.out.println("接收到删除公司请求，ID: " + id);
        try {
            Optional<Company> company = companyRepository.findById(id);
            if (company.isPresent()) {
                // 先删除关联的职位
                entityManager.createQuery("DELETE FROM Job j WHERE j.company.id = :companyId")
                    .setParameter("companyId", id)
                    .executeUpdate();
                
                // 再删除公司
                companyRepository.deleteById(id);
                System.out.println("公司删除成功，ID: " + id);
                return Result.success(null);
            } else {
                System.out.println("公司不存在，ID: " + id);
                return Result.error("公司不存在");
            }
        } catch (Exception e) {
            System.err.println("删除公司时发生错误: " + e.getMessage());
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 切换企业状态（启用/禁用）
     * @param id 企业ID
     * @param enabled 是否启用
     * @return 更新后的企业信息
     */
    @Operation(summary = "切换企业状态", description = "启用或禁用企业")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "操作成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "企业不存在")
    })
    @PutMapping("/{id}/status")
    public Result<Company> toggleStatus(
            @Parameter(description = "企业ID", required = true) @PathVariable Long id,
            @Parameter(description = "是否启用", required = true) @RequestParam Boolean enabled) {
        Optional<Company> company = companyRepository.findById(id);
        if (company.isPresent()) {
            Company companyToUpdate = company.get();
            companyToUpdate.setEnabled(enabled);
            companyToUpdate.setUpdateTime(LocalDateTime.now());
            return Result.success(companyRepository.save(companyToUpdate));
        }
        return Result.error("公司不存在");
    }

    /**
     * 批量删除企业
     * @param request 包含企业ID列表的请求
     * @return 操作结果
     */
    @Operation(summary = "批量删除企业", description = "批量删除多个企业，同时删除关联的职位")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "500", description = "删除失败")
    })
    @DeleteMapping("/batch")
    @Transactional
    public Result<Void> batchDelete(
            @Parameter(description = "包含企业ID列表的请求", required = true) @RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.error("未提供要删除的公司ID");
        }
        
        System.out.println("接收到批量删除公司请求，ID列表: " + ids);
        try {
            // 先删除关联的职位
            entityManager.createQuery("DELETE FROM Job j WHERE j.company.id IN :companyIds")
                .setParameter("companyIds", ids)
                .executeUpdate();
            
            // 再删除公司
            companyRepository.deleteAllById(ids);
            System.out.println("批量删除公司成功，ID列表: " + ids);
            return Result.success(null);
        } catch (Exception e) {
            System.err.println("批量删除公司时发生错误: " + e.getMessage());
            e.printStackTrace();
            return Result.error("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有启用状态的企业
     * @return 启用状态的企业列表
     */
    @Operation(summary = "获取所有启用状态的企业", description = "获取所有状态为启用的企业列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/enabled")
    public Result<List<Company>> listEnabled() {
        List<Company> companies = companyRepository.findAllEnabled();
        return Result.success(companies);
    }
}
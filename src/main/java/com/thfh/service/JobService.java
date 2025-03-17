package com.thfh.service;

import com.thfh.dto.JobDTO;
import com.thfh.dto.JobQueryDTO;
import com.thfh.model.Job;
import com.thfh.model.Company;
import com.thfh.model.JobStatus;
import com.thfh.model.JobApplicationStatus;
import com.thfh.model.JobCategory;
import com.thfh.repository.JobRepository;
import com.thfh.repository.CompanyRepository;
import com.thfh.repository.JobApplicationRepository;
import com.thfh.repository.JobCategoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 职位服务类
 * 提供职位相关的业务逻辑处理，包括职位的创建、查询、修改、删除等操作
 * 以及职位状态管理、发布、关闭等功能
 */
@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;
    
    @Autowired
    private JobCategoryRepository jobCategoryRepository;

    /**
     * 根据查询条件获取职位列表
     * @param queryDTO 查询条件对象，包含职位标题、公司ID、地点、状态、启用状态等过滤条件
     * @return 分页后的职位DTO列表
     */
    public Page<JobDTO> getJobs(JobQueryDTO queryDTO) {
        Specification<Job> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (queryDTO.getTitle() != null && !queryDTO.getTitle().isEmpty()) {
                predicates.add(cb.like(root.get("title"), "%" + queryDTO.getTitle() + "%"));
            }
            if (queryDTO.getCompanyId() != null) {
                predicates.add(cb.equal(root.get("company").get("id"), queryDTO.getCompanyId()));
            }
            if (queryDTO.getCompanyName() != null && !queryDTO.getCompanyName().isEmpty()) {
                predicates.add(cb.like(root.get("company").get("name"), "%" + queryDTO.getCompanyName() + "%"));
            }
            if (queryDTO.getLocation() != null) {
                predicates.add(cb.like(root.get("location"), "%" + queryDTO.getLocation() + "%"));
            }
            if (queryDTO.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), queryDTO.getStatus()));
            }
            if (queryDTO.getEnabled() != null) {
                predicates.add(cb.equal(root.get("enabled"), queryDTO.getEnabled()));
            }
            // 添加分类查询条件
            if (queryDTO.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), queryDTO.getCategoryId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Page<Job> jobPage = jobRepository.findAll(spec,
                PageRequest.of(queryDTO.getPageNum() - 1, queryDTO.getPageSize(), sort));

        return jobPage.map(this::convertToDTO);
    }

    /**
     * 创建新职位
     * @param jobDTO 职位信息对象，包含职位的基本信息
     * @return 创建成功的职位DTO对象
     * @throws RuntimeException 当关联的公司不存在时抛出
     */
    @Transactional
    public JobDTO createJob(JobDTO jobDTO) {
        Company company = companyRepository.findById(jobDTO.getCompanyId())
                .orElseThrow(() -> new RuntimeException("公司不存在"));

        Job job = new Job();
        BeanUtils.copyProperties(jobDTO, job);
        job.setCompany(company);
        
        // 设置职位分类
        if (jobDTO.getCategoryId() != null) {
            JobCategory category = jobCategoryRepository.findById(jobDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("职位分类不存在"));
            job.setCategory(category);
        }
        
        job.setStatus(JobStatus.DRAFT);
        job = jobRepository.save(job);

        return convertToDTO(job);
    }

    /**
     * 更新职位信息
     * @param id 职位ID
     * @param jobDTO 更新后的职位信息对象
     * @return 更新后的职位DTO对象
     * @throws RuntimeException 当职位不存在或关联的公司不存在时抛出
     */
    @Transactional
    public JobDTO updateJob(Long id, JobDTO jobDTO) {
        // 1. 验证职位是否存在
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位不存在"));

        // 2. 验证公司是否存在
        Company company = companyRepository.findById(jobDTO.getCompanyId())
                .orElseThrow(() -> new RuntimeException("公司不存在"));

        // 3. 基本数据验证
        if (jobDTO.getSalaryMin() != null && jobDTO.getSalaryMax() != null 
            && jobDTO.getSalaryMin().compareTo(jobDTO.getSalaryMax()) > 0) {
            throw new RuntimeException("最低薪资不能大于最高薪资");
        }

        // 4. 验证字段长度
        if (jobDTO.getDescription() != null && jobDTO.getDescription().length() > 1000) {
            throw new RuntimeException("职位描述长度不能超过1000个字符");
        }
        if (jobDTO.getTitle() != null && jobDTO.getTitle().length() > 50) {
            throw new RuntimeException("职位标题长度不能超过50个字符");
        }

        // 5. 复制属性时排除不应更新的字段
        BeanUtils.copyProperties(jobDTO, job, 
            "id", "createTime", "viewCount", "applyCount", "applications", "company", "category");

        // 6. 设置关联对象
        job.setCompany(company);
        
        // 设置职位分类
        if (jobDTO.getCategoryId() != null) {
            JobCategory category = jobCategoryRepository.findById(jobDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("职位分类不存在"));
            job.setCategory(category);
        } else {
            job.setCategory(null);
        }

        // 7. 如果状态为空，设置为草稿状态
        if (job.getStatus() == null) {
            job.setStatus(JobStatus.DRAFT);
        }

        // 8. 保存更新
        try {
            job = jobRepository.save(job);
            return convertToDTO(job);
        } catch (Exception e) {
            throw new RuntimeException("保存职位信息失败：" + e.getMessage());
        }
    }

    /**
     * 发布职位
     * 将职位状态从草稿改为已发布状态
     * @param id 职位ID
     * @throws RuntimeException 当职位不存在时抛出
     */
    @Transactional
    public void publishJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位不存在"));
        job.setStatus(JobStatus.PUBLISHED);
        jobRepository.save(job);
    }

    /**
     * 关闭职位
     * 将职位状态改为已关闭状态
     * @param id 职位ID
     * @throws RuntimeException 当职位不存在时抛出
     */
    @Transactional
    public void closeJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位不存在"));
        job.setStatus(JobStatus.CLOSED);
        jobRepository.save(job);
    }

    /**
     * 删除指定ID的职位
     * @param id 要删除的职位ID
     */
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    /**
     * 切换职位启用状态
     * 如果职位当前是启用状态，则禁用；如果是禁用状态，则启用
     * @param id 职位ID
     * @throws RuntimeException 当职位不存在时抛出
     */
    public void toggleJobStatus(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位不存在"));
        job.setEnabled(!job.getEnabled());
        jobRepository.save(job);
    }

    /**
     * 获取职位的各状态申请数量
     * @param jobId 职位ID
     * @return 包含各状态申请数量的DTO对象
     */
    public Map<String, Long> getJobApplicationCounts(Long jobId) {
        Map<String, Long> counts = new HashMap<>();
        counts.put("pending", jobApplicationRepository.countByJobIdAndStatus(jobId, JobApplicationStatus.PENDING));
        counts.put("reviewing", jobApplicationRepository.countByJobIdAndStatus(jobId, JobApplicationStatus.REVIEWING));
        counts.put("interview", jobApplicationRepository.countByJobIdAndStatus(jobId, JobApplicationStatus.INTERVIEW));
        return counts;
    }

    /**
     * 将职位实体对象转换为DTO对象
     * @param job 职位实体对象
     * @return 转换后的职位DTO对象
     */
    private JobDTO convertToDTO(Job job) {
        JobDTO dto = new JobDTO();
        BeanUtils.copyProperties(job, dto);
        dto.setCompanyId(job.getCompany().getId());
        dto.setCompanyName(job.getCompany().getName());
        
        // 设置分类信息
        if (job.getCategory() != null) {
            dto.setCategoryId(job.getCategory().getId());
            dto.setCategoryName(job.getCategory().getName());
        }
        
        return dto;
    }
}
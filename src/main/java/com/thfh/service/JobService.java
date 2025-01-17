package com.thfh.service;

import com.thfh.dto.JobDTO;
import com.thfh.dto.JobQueryDTO;
import com.thfh.model.Job;
import com.thfh.model.Company;
import com.thfh.model.JobStatus;
import com.thfh.repository.JobRepository;
import com.thfh.repository.CompanyRepository;
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

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public Page<JobDTO> getJobs(JobQueryDTO queryDTO) {
        Specification<Job> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (queryDTO.getTitle() != null && !queryDTO.getTitle().isEmpty()) {
                predicates.add(cb.like(root.get("title"), "%" + queryDTO.getTitle() + "%"));
            }
            if (queryDTO.getCompanyId() != null) {
                predicates.add(cb.equal(root.get("company").get("id"), queryDTO.getCompanyId()));
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

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Page<Job> jobPage = jobRepository.findAll(spec,
                PageRequest.of(queryDTO.getPageNum() - 1, queryDTO.getPageSize(), sort));

        return jobPage.map(this::convertToDTO);
    }

    @Transactional
    public JobDTO createJob(JobDTO jobDTO) {
        Company company = companyRepository.findById(jobDTO.getCompanyId())
                .orElseThrow(() -> new RuntimeException("公司不存在"));

        Job job = new Job();
        BeanUtils.copyProperties(jobDTO, job);
        job.setCompany(company);
        job.setStatus(JobStatus.DRAFT);
        job = jobRepository.save(job);

        return convertToDTO(job);
    }

    @Transactional
    public JobDTO updateJob(Long id, JobDTO jobDTO) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位不存在"));

        Company company = companyRepository.findById(jobDTO.getCompanyId())
                .orElseThrow(() -> new RuntimeException("公司不存在"));

        BeanUtils.copyProperties(jobDTO, job, "id", "createTime", "viewCount", "applyCount");
        job.setCompany(company);
        job = jobRepository.save(job);

        return convertToDTO(job);
    }

    @Transactional
    public void publishJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位不存在"));
        job.setStatus(JobStatus.PUBLISHED);
        jobRepository.save(job);
    }

    @Transactional
    public void closeJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位不存在"));
        job.setStatus(JobStatus.CLOSED);
        jobRepository.save(job);
    }

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    public void toggleJobStatus(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位不存在"));
        job.setEnabled(!job.getEnabled());
        jobRepository.save(job);
    }

    private JobDTO convertToDTO(Job job) {
        JobDTO dto = new JobDTO();
        BeanUtils.copyProperties(job, dto);
        dto.setCompanyId(job.getCompany().getId());
        dto.setCompanyName(job.getCompany().getName());
        return dto;
    }
}
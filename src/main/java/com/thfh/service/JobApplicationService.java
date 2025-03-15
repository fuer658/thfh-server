package com.thfh.service;

import com.thfh.dto.JobApplicationDTO;
import com.thfh.dto.JobApplicationQueryDTO;
import com.thfh.model.Job;
import com.thfh.model.JobApplication;
import com.thfh.model.JobApplicationStatus;
import com.thfh.model.User;
import com.thfh.repository.JobApplicationRepository;
import com.thfh.repository.JobRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 职位申请服务类
 * 提供职位申请相关的业务逻辑处理
 */
@Service
public class JobApplicationService {
    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 创建职位申请
     * @param jobApplicationDTO 职位申请信息
     * @return 创建后的职位申请DTO
     */
    @Transactional
    public JobApplicationDTO createJobApplication(JobApplicationDTO jobApplicationDTO) {
        // 检查用户是否存在
        User user = userRepository.findById(jobApplicationDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 检查职位是否存在且是已发布状态
        Job job = jobRepository.findById(jobApplicationDTO.getJobId())
                .orElseThrow(() -> new RuntimeException("职位不存在"));


        // 创建新的职位申请
        JobApplication jobApplication = new JobApplication();
        jobApplication.setJob(job);
        jobApplication.setUser(user);
        jobApplication.setResumeUrl(jobApplicationDTO.getResumeUrl());
        jobApplication.setCoverLetter(jobApplicationDTO.getCoverLetter());
        jobApplication.setStatus(JobApplicationStatus.PENDING);
        jobApplication.setIsRead(false);

        // 保存职位申请
        jobApplication = jobApplicationRepository.save(jobApplication);

        // 更新职位申请数量
        job.setApplyCount(job.getApplyCount() + 1);
        jobRepository.save(job);

        return convertToDTO(jobApplication);
    }

    /**
     * 根据查询条件获取职位申请列表
     * @param queryDTO 查询条件
     * @return 分页后的职位申请DTO列表
     */
    public Page<JobApplicationDTO> getJobApplications(JobApplicationQueryDTO queryDTO) {
        Specification<JobApplication> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (queryDTO.getJobId() != null) {
                predicates.add(cb.equal(root.get("job").get("id"), queryDTO.getJobId()));
            }
            if (queryDTO.getUserId() != null) {
                predicates.add(cb.equal(root.get("user").get("id"), queryDTO.getUserId()));
            }
            if (queryDTO.getCompanyId() != null) {
                predicates.add(cb.equal(root.get("job").get("company").get("id"), queryDTO.getCompanyId()));
            }
            if (queryDTO.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), queryDTO.getStatus()));
            }
            if (queryDTO.getIsRead() != null) {
                predicates.add(cb.equal(root.get("isRead"), queryDTO.getIsRead()));
            }
            if (queryDTO.getStartTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"), queryDTO.getStartTime()));
            }
            if (queryDTO.getEndTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime"), queryDTO.getEndTime()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Page<JobApplication> applicationPage = jobApplicationRepository.findAll(spec,
                PageRequest.of(queryDTO.getPageNum() - 1, queryDTO.getPageSize(), sort));

        return applicationPage.map(this::convertToDTO);
    }

    /**
     * 根据ID获取职位申请详情
     * @param id 职位申请ID
     * @return 职位申请DTO
     */
    public JobApplicationDTO getJobApplicationById(Long id) {
        JobApplication jobApplication = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位申请不存在"));
        return convertToDTO(jobApplication);
    }

    /**
     * 更新职位申请状态
     * @param id 职位申请ID
     * @param status 新状态
     * @param reason 状态变更原因（如拒绝原因）
     * @return 更新后的职位申请DTO
     */
    @Transactional
    public JobApplicationDTO updateJobApplicationStatus(Long id, JobApplicationStatus status, String reason) {
        JobApplication jobApplication = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位申请不存在"));

        jobApplication.setStatus(status);
        if (status == JobApplicationStatus.REJECTED && reason != null) {
            jobApplication.setRejectionReason(reason);
        }

        jobApplication = jobApplicationRepository.save(jobApplication);
        return convertToDTO(jobApplication);
    }

    /**
     * 安排面试
     * @param id 职位申请ID
     * @param interviewTime 面试时间
     * @param interviewLocation 面试地点
     * @param notes 面试备注
     * @return 更新后的职位申请DTO
     */
    @Transactional
    public JobApplicationDTO arrangeInterview(Long id, LocalDateTime interviewTime, String interviewLocation, String notes) {
        JobApplication jobApplication = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位申请不存在"));

        jobApplication.setStatus(JobApplicationStatus.INTERVIEW);
        jobApplication.setInterviewTime(interviewTime);
        jobApplication.setInterviewLocation(interviewLocation);
        jobApplication.setNotes(notes);

        jobApplication = jobApplicationRepository.save(jobApplication);
        return convertToDTO(jobApplication);
    }

    /**
     * 标记申请为已读
     * @param id 职位申请ID
     * @return 更新后的职位申请DTO
     */
    @Transactional
    public JobApplicationDTO markAsRead(Long id) {
        JobApplication jobApplication = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位申请不存在"));

        if (!jobApplication.getIsRead()) {
            jobApplication.setIsRead(true);
            jobApplication = jobApplicationRepository.save(jobApplication);
        }

        return convertToDTO(jobApplication);
    }

    /**
     * 撤回职位申请
     * @param id 职位申请ID
     * @param userId 用户ID（用于验证是否是申请人）
     * @return 更新后的职位申请DTO
     */
    @Transactional
    public JobApplicationDTO withdrawApplication(Long id, Long userId) {
        JobApplication jobApplication = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位申请不存在"));

        // 验证是否是申请人本人
        if (!jobApplication.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权操作此申请");
        }

        // 只有处于待处理、审核中或面试中状态的申请才能撤回
        if (jobApplication.getStatus() == JobApplicationStatus.PENDING || 
            jobApplication.getStatus() == JobApplicationStatus.REVIEWING || 
            jobApplication.getStatus() == JobApplicationStatus.INTERVIEW) {
            jobApplication.setStatus(JobApplicationStatus.WITHDRAWN);
            jobApplication = jobApplicationRepository.save(jobApplication);
        } else {
            throw new RuntimeException("当前状态无法撤回申请");
        }

        return convertToDTO(jobApplication);
    }

    /**
     * 删除职位申请
     * @param id 职位申请ID
     */
    @Transactional
    public void deleteJobApplication(Long id) {
        JobApplication jobApplication = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位申请不存在"));
        
        // 更新职位申请计数
        Job job = jobApplication.getJob();
        if (job.getApplyCount() > 0) {
            job.setApplyCount(job.getApplyCount() - 1);
            jobRepository.save(job);
        }
        
        jobApplicationRepository.delete(jobApplication);
    }

    /**
     * 更新申请备注
     * @param id 职位申请ID
     * @param notes 备注内容
     * @return 更新后的职位申请DTO
     */
    @Transactional
    public JobApplicationDTO updateNotes(Long id, String notes) {
        JobApplication jobApplication = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("职位申请不存在"));
        
        jobApplication.setNotes(notes);
        jobApplication = jobApplicationRepository.save(jobApplication);
        
        return convertToDTO(jobApplication);
    }

    /**
     * 获取职位未读申请数量
     * @param jobId 职位ID
     * @return 未读申请数量
     */
    public long getUnreadApplicationCount(Long jobId) {
        // 验证职位是否存在
        jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("职位不存在"));
                
        // 使用Specification查询未读的申请数量
        Specification<JobApplication> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("job").get("id"), jobId));
            predicates.add(cb.equal(root.get("isRead"), false));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return jobApplicationRepository.count(spec);
    }

    /**
     * 获取公司未读申请数量
     * @param companyId 公司ID
     * @return 未读申请数量
     */
    public long getCompanyUnreadApplicationCount(Long companyId) {
        // 使用Specification查询公司未读的申请数量
        Specification<JobApplication> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("job").get("company").get("id"), companyId));
            predicates.add(cb.equal(root.get("isRead"), false));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return jobApplicationRepository.count(spec);
    }

    /**
     * 将JobApplication实体转换为DTO
     * @param jobApplication 职位申请实体
     * @return 职位申请DTO
     */
    private JobApplicationDTO convertToDTO(JobApplication jobApplication) {
        JobApplicationDTO dto = new JobApplicationDTO();
        BeanUtils.copyProperties(jobApplication, dto);
        
        dto.setJobId(jobApplication.getJob().getId());
        dto.setJobTitle(jobApplication.getJob().getTitle());
        dto.setUserId(jobApplication.getUser().getId());
        dto.setUsername(jobApplication.getUser().getUsername());
        dto.setRealName(jobApplication.getUser().getRealName());
        dto.setAvatar(jobApplication.getUser().getAvatar());
        
        return dto;
    }
} 
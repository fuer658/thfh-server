package com.thfh.repository;

import com.thfh.model.JobApplication;
import com.thfh.model.JobApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 职位申请数据访问接口
 * 提供对职位申请(JobApplication)实体的数据库操作功能
 */
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long>, JpaSpecificationExecutor<JobApplication> {
    /**
     * 查找用户对特定职位的申请记录
     * @param jobId 职位ID
     * @param userId 用户ID
     * @return 申请记录列表
     */
    List<JobApplication> findByJobIdAndUserId(Long jobId, Long userId);
    
    /**
     * 查找用户的所有申请记录（分页）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页的申请记录
     */
    Page<JobApplication> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 查找特定职位的所有申请记录（分页）
     * @param jobId 职位ID
     * @param pageable 分页参数
     * @return 分页的申请记录
     */
    Page<JobApplication> findByJobId(Long jobId, Pageable pageable);
    
    /**
     * 查找特定公司所有职位的申请记录（分页）
     * @param companyId 公司ID
     * @param pageable 分页参数
     * @return 分页的申请记录
     */
    @Query("SELECT ja FROM JobApplication ja WHERE ja.job.company.id = :companyId")
    Page<JobApplication> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);
    
    /**
     * 计算特定职位的申请数量
     * @param jobId 职位ID
     * @return 申请数量
     */
    long countByJobId(Long jobId);
    
    /**
     * 计算特定状态的申请数量
     * @param status 申请状态
     * @return 申请数量
     */
    long countByStatus(JobApplicationStatus status);
    
    /**
     * 查找特定状态的申请记录（分页）
     * @param status 申请状态
     * @param pageable 分页参数
     * @return 分页的申请记录
     */
    Page<JobApplication> findByStatus(JobApplicationStatus status, Pageable pageable);
    
    /**
     * 查找特定状态且未读的申请记录（分页）
     * @param status 申请状态
     * @param isRead 是否已读
     * @param pageable 分页参数
     * @return 分页的申请记录
     */
    Page<JobApplication> findByStatusAndIsRead(JobApplicationStatus status, Boolean isRead, Pageable pageable);
    
    /**
     * 查找公司未读的申请记录数量
     * @param companyId 公司ID
     * @param isRead 是否已读
     * @return 未读申请数量
     */
    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.job.company.id = :companyId AND ja.isRead = :isRead")
    long countByCompanyIdAndIsRead(@Param("companyId") Long companyId, @Param("isRead") Boolean isRead);

    /**
     * 统计指定职位的特定状态的申请数量
     * @param jobId 职位ID
     * @param status 申请状态
     * @return 申请数量
     */
    long countByJobIdAndStatus(Long jobId, JobApplicationStatus status);
} 
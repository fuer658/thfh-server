package com.thfh.repository;

import com.thfh.model.UserUpload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserUploadRepository extends JpaRepository<UserUpload, Long> {
    
    /**
     * 查询用户的所有上传文件
     * @param userId 用户ID
     * @return 上传文件列表
     */
    List<UserUpload> findByUserIdAndIsEnabledOrderByUploadTimeDesc(Long userId, Boolean isEnabled);
    
    /**
     * 根据用户ID和文件类型查询
     * @param userId 用户ID
     * @param fileType 文件类型
     * @return 上传文件列表
     */
    List<UserUpload> findByUserIdAndFileTypeAndIsEnabledOrderByUploadTimeDesc(Long userId, String fileType, Boolean isEnabled);
    
    /**
     * 根据用户ID和分类查询
     * @param userId 用户ID
     * @param category 分类名称
     * @return 上传文件列表
     */
    List<UserUpload> findByUserIdAndCategoryAndIsEnabledOrderByUploadTimeDesc(Long userId, String category, Boolean isEnabled);
    
    /**
     * 根据用户ID和文件类型及分类查询
     * @param userId 用户ID
     * @param fileType 文件类型
     * @param category 分类名称
     * @return 上传文件列表
     */
    List<UserUpload> findByUserIdAndFileTypeAndCategoryAndIsEnabledOrderByUploadTimeDesc(
            Long userId, String fileType, String category, Boolean isEnabled);
    
    /**
     * 分页查询用户的上传文件
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<UserUpload> findByUserIdAndIsEnabled(Long userId, Boolean isEnabled, Pageable pageable);
    
    /**
     * 模糊查询用户的上传文件
     * @param userId 用户ID
     * @param keyword 关键词（文件名或描述）
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query("SELECT u FROM UserUpload u WHERE u.userId = :userId AND u.isEnabled = true AND " +
           "(u.fileName LIKE %:keyword% OR u.description LIKE %:keyword%)")
    Page<UserUpload> searchByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);
} 
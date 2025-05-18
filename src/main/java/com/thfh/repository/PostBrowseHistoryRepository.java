package com.thfh.repository;

import com.thfh.model.PostBrowseHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostBrowseHistoryRepository extends JpaRepository<PostBrowseHistory, Long> {
    
    /**
     * 根据用户ID查找浏览记录
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 用户的动态浏览记录
     */
    Page<PostBrowseHistory> findByUserIdOrderByLastBrowseTimeDesc(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和动态ID查找浏览记录
     * @param userId 用户ID
     * @param postId 动态ID
     * @return 浏览记录
     */
    Optional<PostBrowseHistory> findByUserIdAndPostId(Long userId, Long postId);
    
    /**
     * 查询用户最近浏览的动态ID列表
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 动态ID列表
     */
    @Query("SELECT h.postId FROM PostBrowseHistory h WHERE h.userId = :userId ORDER BY h.lastBrowseTime DESC")
    List<Long> findRecentBrowsedPostIdsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 删除用户特定动态的浏览记录
     * @param userId 用户ID
     * @param postId 动态ID
     * @return 影响的行数
     */
    int deleteByUserIdAndPostId(Long userId, Long postId);
    
    /**
     * 删除用户的所有浏览记录
     * @param userId 用户ID
     * @return 影响的行数
     */
    int deleteByUserId(Long userId);
} 
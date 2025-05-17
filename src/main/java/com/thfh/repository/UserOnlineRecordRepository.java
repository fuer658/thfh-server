package com.thfh.repository;

import com.thfh.model.User;
import com.thfh.model.UserOnlineRecord;
import com.thfh.model.UserOnlineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户在线状态记录数据访问接口
 */
@Repository
public interface UserOnlineRecordRepository extends JpaRepository<UserOnlineRecord, Long> {
    
    /**
     * 根据用户ID查找最近的在线状态记录
     * 
     * @param userId 用户ID
     * @return 最近的在线状态记录（可能为空）
     */
    @Query("SELECT r FROM UserOnlineRecord r WHERE r.user.id = :userId ORDER BY r.updateTime DESC")
    List<UserOnlineRecord> findLatestByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户查找最近的在线状态记录
     * 
     * @param user 用户实体
     * @return 最近的在线状态记录（可能为空）
     */
    Optional<UserOnlineRecord> findTopByUserOrderByUpdateTimeDesc(User user);
    
    /**
     * 查找特定状态的用户记录
     * 
     * @param status 在线状态
     * @return 符合条件的记录列表
     */
    List<UserOnlineRecord> findByStatus(UserOnlineStatus status);
    
    /**
     * 查找一段时间内活跃的用户记录
     * 
     * @param fromTime 开始时间
     * @param toTime 结束时间
     * @return 符合条件的记录列表
     */
    @Query("SELECT r FROM UserOnlineRecord r WHERE r.lastActive BETWEEN :fromTime AND :toTime")
    List<UserOnlineRecord> findActiveUsersBetween(
            @Param("fromTime") LocalDateTime fromTime, 
            @Param("toTime") LocalDateTime toTime);
    
    /**
     * 根据用户ID列表批量查询用户在线状态
     * 
     * @param userIds 用户ID列表
     * @return 符合条件的记录列表
     */
    @Query("SELECT r FROM UserOnlineRecord r WHERE r.user.id IN :userIds ORDER BY r.user.id, r.updateTime DESC")
    List<UserOnlineRecord> findLatestByUserIds(@Param("userIds") List<Long> userIds);
} 
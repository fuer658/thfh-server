package com.thfh.repository;

import com.thfh.model.CheckIn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    
    @Query("SELECT c FROM CheckIn c WHERE c.userId = :userId AND DATE(c.checkInTime) = CURRENT_DATE")
    List<CheckIn> findTodayCheckIn(@Param("userId") Long userId);
    
    @Query("SELECT c FROM CheckIn c WHERE c.userId = :userId AND CAST(c.checkInTime AS date) = :checkInDate")
    List<CheckIn> findByUserIdAndDate(@Param("userId") Long userId, @Param("checkInDate") LocalDate checkInDate);
    
    @Query("SELECT COUNT(c) FROM CheckIn c WHERE c.userId = :userId AND YEAR(c.checkInTime) = YEAR(CURRENT_DATE) AND MONTH(c.checkInTime) = MONTH(CURRENT_DATE)")
    Long countMonthlyCheckIns(@Param("userId") Long userId);
    
    @Query("SELECT c FROM CheckIn c WHERE c.userId = :userId AND YEAR(c.checkInTime) = YEAR(CURRENT_DATE) AND MONTH(c.checkInTime) = MONTH(CURRENT_DATE) ORDER BY c.checkInTime DESC")
    List<CheckIn> findMonthlyCheckIns(@Param("userId") Long userId);
    
    Page<CheckIn> findByUserIdOrderByCheckInTimeDesc(Long userId, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM CheckIn c WHERE c.userId = :userId AND c.checkInTime BETWEEN :startDate AND :endDate")
    Long countCheckInsBetweenDates(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT c FROM CheckIn c WHERE c.userId = :userId AND c.checkInTime BETWEEN :startOfDay AND :endOfDay")
    List<CheckIn> findByUserIdAndDateRange(
        @Param("userId") Long userId, 
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay);
}
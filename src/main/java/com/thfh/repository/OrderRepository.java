package com.thfh.repository;

import com.thfh.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.artwork a " +
            "WHERE (:orderNo is null OR o.orderNo LIKE CONCAT('%', :orderNo, '%')) " +
            "AND (:username is null OR u.username LIKE CONCAT('%', :username, '%')) " +
            "AND (:status is null OR o.status = :status)")
    List<Order> findByConditionWithJoinFetch(@Param("orderNo") String orderNo,
                                             @Param("username") String username,
                                             @Param("status") String status);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN o.user u " +
            "WHERE (:orderNo is null OR o.orderNo LIKE CONCAT('%', :orderNo, '%')) " +
            "AND (:username is null OR u.username LIKE CONCAT('%', :username, '%')) " +
            "AND (:status is null OR o.status = :status)")
    Page<Order> findByCondition(@Param("orderNo") String orderNo,
                                @Param("username") String username,
                                @Param("status") String status,
                                Pageable pageable);

    @Modifying
    @Query("UPDATE Order o SET o.status = :status, o.updateTime = CURRENT_TIMESTAMP WHERE o.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") String status);

    @Modifying
    @Query("UPDATE Order o SET o.logisticsCompany = :company, o.logisticsNo = :number, " +
            "o.updateTime = CURRENT_TIMESTAMP WHERE o.id = :id")
    void updateLogistics(@Param("id") Long id,
                         @Param("company") String company,
                         @Param("number") String number);
}
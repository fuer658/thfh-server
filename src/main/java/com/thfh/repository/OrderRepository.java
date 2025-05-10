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

/**
 * 订单数据访问接口
 * 提供对订单(Order)实体的数据库操作功能
 * 继承自JpaRepository，自动提供基本的CRUD操作
 * 实现JpaSpecificationExecutor接口，支持复杂条件查询
 */
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    /**
     * 根据条件查询订单，并预加载关联的用户和艺术品信息
     * 使用JOIN FETCH优化查询性能，避免N+1问题
     * 
     * @param orderNo 订单号（模糊匹配，可为null）
     * @param username 用户名（模糊匹配，可为null）
     * @param status 订单状态（精确匹配，可为null）
     * @return 满足条件的订单列表，包含预加载的关联实体
     */
    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.artwork a " +
            "WHERE (:orderNo is null OR o.orderNo LIKE CONCAT('%', :orderNo, '%')) " +
            "AND (:username is null OR u.username LIKE CONCAT('%', :username, '%')) " +
            "AND (:status is null OR o.status = :status)")
    List<Order> findByConditionWithJoinFetch(@Param("orderNo") String orderNo,
                                             @Param("username") String username,
                                             @Param("status") String status);

    /**
     * 根据条件分页查询订单
     * 
     * @param orderNo 订单号（模糊匹配，可为null）
     * @param username 用户名（模糊匹配，可为null）
     * @param status 订单状态（精确匹配，可为null）
     * @param pageable 分页参数
     * @return 分页后的订单列表
     */
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN o.user u " +
            "WHERE (:orderNo is null OR o.orderNo LIKE CONCAT('%', :orderNo, '%')) " +
            "AND (:username is null OR u.username LIKE CONCAT('%', :username, '%')) " +
            "AND (:status is null OR o.status = :status)")
    Page<Order> findByCondition(@Param("orderNo") String orderNo,
                                @Param("username") String username,
                                @Param("status") String status,
                                Pageable pageable);

    /**
     * 更新订单状态
     * 同时更新订单的更新时间为当前时间
     * 
     * @param id 订单ID
     * @param status 新的订单状态
     */
    @Modifying
    @Query("UPDATE Order o SET o.status = :status, o.updateTime = CURRENT_TIMESTAMP WHERE o.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 更新订单物流信息
     * 同时更新订单的更新时间为当前时间
     * 
     * @param id 订单ID
     * @param company 物流公司名称
     * @param number 物流单号
     */
    @Modifying
    @Query("UPDATE Order o SET o.logisticsCompany = :company, o.logisticsNo = :number, " +
            "o.updateTime = CURRENT_TIMESTAMP WHERE o.id = :id")
    void updateLogistics(@Param("id") Long id,
                         @Param("company") String company,
                         @Param("number") String number);

    /**
     * 根据用户ID分页查询订单
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 订单分页列表
     */
    Page<Order> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID和状态分页查询订单
     * @param userId 用户ID
     * @param status 订单状态
     * @param pageable 分页参数
     * @return 订单分页列表
     */
    Page<Order> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
}
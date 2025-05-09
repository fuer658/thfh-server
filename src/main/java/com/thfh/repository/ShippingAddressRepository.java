package com.thfh.repository;

import com.thfh.model.ShippingAddress;
import com.thfh.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 收货地址数据访问接口
 */
@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {
    
    /**
     * 根据用户查询所有收货地址
     * @param user 用户实体
     * @return 收货地址列表
     */
    List<ShippingAddress> findByUser(User user);
    
    /**
     * 根据用户ID查询所有收货地址
     * @param userId 用户ID
     * @return 收货地址列表
     */
    List<ShippingAddress> findByUserId(Long userId);
    
    /**
     * 根据用户查询默认收货地址
     * @param user 用户实体
     * @return 默认收货地址
     */
    Optional<ShippingAddress> findByUserAndIsDefaultTrue(User user);
    
    /**
     * 根据用户ID查询默认收货地址
     * @param userId 用户ID
     * @return 默认收货地址
     */
    Optional<ShippingAddress> findByUserIdAndIsDefaultTrue(Long userId);
    
    /**
     * 根据ID和用户查询收货地址
     * @param id 收货地址ID
     * @param user 用户实体
     * @return 收货地址
     */
    Optional<ShippingAddress> findByIdAndUser(Long id, User user);
    
    /**
     * 根据ID和用户ID查询收货地址
     * @param id 收货地址ID
     * @param userId 用户ID
     * @return 收货地址
     */
    Optional<ShippingAddress> findByIdAndUserId(Long id, Long userId);
    
    /**
     * 清除用户的所有默认地址标记
     * @param userId 用户ID
     * @return 更新的记录数
     */
    @Modifying
    @Query("UPDATE ShippingAddress sa SET sa.isDefault = false WHERE sa.user.id = :userId AND sa.isDefault = true")
    int clearDefaultAddress(Long userId);
    
    /**
     * 删除用户的收货地址
     * @param id 收货地址ID
     * @param userId 用户ID
     * @return 删除的记录数
     */
    int deleteByIdAndUserId(Long id, Long userId);
} 
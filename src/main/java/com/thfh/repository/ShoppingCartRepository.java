package com.thfh.repository;

import com.thfh.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    
    /**
     * 根据用户ID查找购物车
     * 
     * @param userId 用户ID
     * @return 购物车对象
     */
    Optional<ShoppingCart> findByUserId(Long userId);
} 
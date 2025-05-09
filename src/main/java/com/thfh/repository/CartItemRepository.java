package com.thfh.repository;

import com.thfh.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    /**
     * 根据购物车ID和作品ID查找购物车项
     * 
     * @param cartId 购物车ID
     * @param artworkId 作品ID
     * @return 购物车项对象
     */
    Optional<CartItem> findByCart_IdAndArtworkId(Long cartId, Long artworkId);
} 
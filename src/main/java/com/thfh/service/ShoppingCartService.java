package com.thfh.service;

import com.thfh.dto.CartItemDTO;
import com.thfh.dto.ShoppingCartDTO;
import com.thfh.model.ShoppingCart;

public interface ShoppingCartService {
    
    /**
     * 获取用户的购物车
     * 
     * @param userId 用户ID
     * @return 购物车DTO
     */
    ShoppingCartDTO getCartByUserId(Long userId);
    
    /**
     * 添加作品到购物车
     * 
     * @param userId 用户ID
     * @param artworkId 作品ID
     * @param quantity 数量
     * @return 更新后的购物车DTO
     */
    ShoppingCartDTO addToCart(Long userId, Long artworkId, Integer quantity);
    
    /**
     * 更新购物车中的作品数量
     * 
     * @param userId 用户ID
     * @param cartItemId 购物车项ID
     * @param quantity 新数量
     * @return 更新后的购物车DTO
     */
    ShoppingCartDTO updateItemQuantity(Long userId, Long cartItemId, Integer quantity);
    
    /**
     * 从购物车中删除作品
     * 
     * @param userId 用户ID
     * @param cartItemId 购物车项ID
     * @return 更新后的购物车DTO
     */
    ShoppingCartDTO removeFromCart(Long userId, Long cartItemId);
    
    /**
     * 清空购物车
     * 
     * @param userId 用户ID
     * @return 更新后的购物车DTO
     */
    ShoppingCartDTO clearCart(Long userId);
    
    /**
     * 将ShoppingCart转换为ShoppingCartDTO
     * 
     * @param cart 购物车实体
     * @return 购物车DTO
     */
    ShoppingCartDTO convertToDTO(ShoppingCart cart);
} 
package com.thfh.service;

import com.thfh.dto.CartItemDTO;
import com.thfh.dto.ShoppingCartDTO;
import com.thfh.model.Artwork;
import com.thfh.model.CartItem;
import com.thfh.model.ShoppingCart;
import com.thfh.repository.CartItemRepository;
import com.thfh.repository.ShoppingCartRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ArtworkService artworkService;

    @Override
    public ShoppingCartDTO getCartByUserId(Long userId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUserId(userId);
                    return shoppingCartRepository.save(newCart);
                });
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDTO addToCart(Long userId, Long artworkId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }

        // 获取作品信息
        Artwork artwork = artworkService.getArtworkById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("作品不存在"));

        // 获取或创建购物车
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUserId(userId);
                    return shoppingCartRepository.save(newCart);
                });

        // 检查购物车中是否已存在该作品
        CartItem cartItem = cartItemRepository.findByCart_IdAndArtworkId(cart.getId(), artworkId)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setArtworkId(artworkId);
                    newItem.setTitle(artwork.getTitle());
                    newItem.setCoverUrl(artwork.getCoverUrl());
                    newItem.setPrice(artwork.getPrice());
                    cart.getItems().add(newItem);
                    return newItem;
                });

        // 更新数量
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItemRepository.save(cartItem);

        // 重新计算总价
        cart.calculateTotalPrice();
        shoppingCartRepository.save(cart);

        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDTO updateItemQuantity(Long userId, Long cartItemId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }

        // 获取购物车
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("购物车不存在"));

        // 查找并更新购物车项
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("购物车项不存在"));

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        // 重新计算总价
        cart.calculateTotalPrice();
        shoppingCartRepository.save(cart);

        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDTO removeFromCart(Long userId, Long cartItemId) {
        // 获取购物车
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("购物车不存在"));

        // 查找并删除购物车项
        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        cartItemRepository.deleteById(cartItemId);

        // 重新计算总价
        cart.calculateTotalPrice();
        shoppingCartRepository.save(cart);

        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDTO clearCart(Long userId) {
        // 获取购物车
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("购物车不存在"));

        // 清空购物车项
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        shoppingCartRepository.save(cart);

        return convertToDTO(cart);
    }

    @Override
    public ShoppingCartDTO convertToDTO(ShoppingCart cart) {
        ShoppingCartDTO dto = new ShoppingCartDTO();
        BeanUtils.copyProperties(cart, dto);

        // 转换购物车项
        List<CartItemDTO> itemDTOs = cart.getItems().stream().map(item -> {
            CartItemDTO itemDTO = new CartItemDTO();
            BeanUtils.copyProperties(item, itemDTO);
            // 计算小计
            itemDTO.setSubtotal(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            return itemDTO;
        }).collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }
} 
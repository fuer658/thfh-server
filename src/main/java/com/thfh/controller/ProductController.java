package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.ProductDTO;
import com.thfh.dto.ProductSearchDTO;
import com.thfh.model.Product;
import com.thfh.model.ProductStatus;
import com.thfh.model.User;
import com.thfh.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        return Result.success(productService.createProduct(productDTO));
    }

    @GetMapping("/{id}")
    public Result<ProductDTO> getProduct(@PathVariable Long id) {
        return Result.success(productService.getProduct(id));
    }

    @GetMapping
    public Result<Page<Product>> getAllProducts(Pageable pageable) {
        return Result.success(productService.getAllProducts(pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        return Result.success(productService.updateProduct(id, productDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success(null);
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public Result<Page<Product>> searchProducts(@Valid ProductSearchDTO searchDTO) {
        return Result.success(productService.searchProducts(searchDTO));
    }

    /**
     * 更新商品状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Result<ProductDTO> updateProductStatus(
            @PathVariable Long id,
            @RequestParam ProductStatus status) {
        return Result.success(productService.updateProductStatus(id, status));
    }

    /**
     * 获取指定状态的商品
     */
    @GetMapping("/status/{status}")
    public Result<Page<Product>> getProductsByStatus(
            @PathVariable ProductStatus status,
            Pageable pageable) {
        return Result.success(productService.getProductsByStatus(status, pageable));
    }

    @PostMapping("/{id}/like")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Void> likeProduct(@PathVariable("id") Long productId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        productService.likeProduct(productId, user.getId());
        return Result.success(null);
    }

    @DeleteMapping("/{id}/like")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Void> unlikeProduct(@PathVariable("id") Long productId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        productService.unlikeProduct(productId, user.getId());
        return Result.success(null);
    }

    @PostMapping("/{id}/favorite")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Void> favoriteProduct(@PathVariable("id") Long productId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        productService.favoriteProduct(productId, user.getId());
        return Result.success(null);
    }

    @DeleteMapping("/{id}/favorite")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Void> unfavoriteProduct(@PathVariable("id") Long productId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        productService.unfavoriteProduct(productId, user.getId());
        return Result.success(null);
    }
    
    @GetMapping("/favorites")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Page<Product>> getUserFavorites(Pageable pageable, @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        return Result.success(productService.getUserFavorites(user.getId(), pageable));
    }
    
    @GetMapping("/{id}/favorite/status")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Result<Map<String, Boolean>> checkFavoriteStatus(@PathVariable("id") Long productId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        boolean isFavorited = productService.isProductFavorited(productId, user.getId());
        Map<String, Boolean> response = new HashMap<>();
        response.put("favorited", isFavorited);
        return Result.success(response);
    }
} 
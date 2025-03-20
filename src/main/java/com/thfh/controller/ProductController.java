package com.thfh.controller;

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
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.createProduct(productDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(@Valid ProductSearchDTO searchDTO) {
        return ResponseEntity.ok(productService.searchProducts(searchDTO));
    }

    /**
     * 更新商品状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductDTO> updateProductStatus(
            @PathVariable Long id,
            @RequestParam ProductStatus status) {
        return ResponseEntity.ok(productService.updateProductStatus(id, status));
    }

    /**
     * 获取指定状态的商品
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Product>> getProductsByStatus(
            @PathVariable ProductStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByStatus(status, pageable));
    }

    @PostMapping("/{id}/like")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> likeProduct(@PathVariable("id") Long productId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        productService.likeProduct(productId, user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> unlikeProduct(@PathVariable("id") Long productId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        productService.unlikeProduct(productId, user.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/favorite")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> favoriteProduct(@PathVariable("id") Long productId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        productService.favoriteProduct(productId, user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/favorite")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> unfavoriteProduct(@PathVariable("id") Long productId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        productService.unfavoriteProduct(productId, user.getId());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/favorites")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Page<Product>> getUserFavorites(Pageable pageable, @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        return ResponseEntity.ok(productService.getUserFavorites(user.getId(), pageable));
    }
    
    @GetMapping("/{id}/favorite/status")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Map<String, Boolean>> checkFavoriteStatus(@PathVariable("id") Long productId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        boolean isFavorited = productService.isProductFavorited(productId, user.getId());
        Map<String, Boolean> response = new HashMap<>();
        response.put("favorited", isFavorited);
        return ResponseEntity.ok(response);
    }
} 
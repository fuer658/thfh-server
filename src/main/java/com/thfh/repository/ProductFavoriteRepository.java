package com.thfh.repository;

import com.thfh.model.ProductFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProductFavoriteRepository extends JpaRepository<ProductFavorite, Long> {
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
    Page<ProductFavorite> findByUserId(Long userId, Pageable pageable);
} 
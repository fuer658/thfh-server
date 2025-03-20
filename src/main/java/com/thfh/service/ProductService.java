package com.thfh.service;

import com.thfh.dto.ProductDTO;
import com.thfh.dto.ProductSearchDTO;
import com.thfh.model.Product;
import com.thfh.model.ProductStatus;
import com.thfh.model.ProductLike;
import com.thfh.model.ProductFavorite;
import com.thfh.model.User;
import com.thfh.repository.ProductRepository;
import com.thfh.repository.ProductLikeRepository;
import com.thfh.repository.ProductFavoriteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductLikeRepository productLikeRepository;

    @Autowired
    private ProductFavoriteRepository productFavoriteRepository;

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        product.setKeywords(generateKeywords(product));
        product = productRepository.save(product);
        BeanUtils.copyProperties(product, productDTO);
        return productDTO;
    }

    @Transactional(readOnly = true)
    public ProductDTO getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("商品不存在"));
        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(product, productDTO);
        return productDTO;
    }

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("商品不存在"));
        BeanUtils.copyProperties(productDTO, product, "id");
        product.setKeywords(generateKeywords(product));
        product = productRepository.save(product);
        BeanUtils.copyProperties(product, productDTO);
        return productDTO;
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("商品不存在");
        }
        productRepository.deleteById(id);
    }

    /**
     * 搜索商品
     */
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(ProductSearchDTO searchDTO) {
        Pageable pageable = PageRequest.of(
            searchDTO.getPageNum() - 1,
            searchDTO.getPageSize(),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return productRepository.searchProducts(
            searchDTO.getKeyword(),
            searchDTO.getCategory(),
            searchDTO.getStatus(),
            searchDTO.getMinPrice(),
            searchDTO.getMaxPrice(),
            pageable
        );
    }

    /**
     * 更新商品状态
     */
    @Transactional
    public ProductDTO updateProductStatus(Long id, ProductStatus status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("商品不存在"));
        product.setStatus(status);
        product = productRepository.save(product);
        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(product, productDTO);
        return productDTO;
    }

    /**
     * 获取指定状态的商品
     */
    @Transactional(readOnly = true)
    public Page<Product> getProductsByStatus(ProductStatus status, Pageable pageable) {
        return productRepository.findByStatus(status, pageable);
    }

    /**
     * 生成商品搜索关键词
     */
    private String generateKeywords(Product product) {
        StringBuilder keywords = new StringBuilder();
        keywords.append(product.getName()).append(" ");
        if (product.getDescription() != null) {
            keywords.append(product.getDescription()).append(" ");
        }
        if (product.getCategory() != null) {
            keywords.append(product.getCategory());
        }
        return keywords.toString().toLowerCase();
    }

    @Transactional
    public void likeProduct(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (!productLikeRepository.existsByUserIdAndProductId(userId, productId)) {
            ProductLike like = new ProductLike();
            like.setProduct(product);
            User user = new User();
            user.setId(userId);
            like.setUser(user);
            productLikeRepository.save(like);

            product.setLikeCount(product.getLikeCount() + 1);
            productRepository.save(product);
        }
    }

    @Transactional
    public void unlikeProduct(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (productLikeRepository.existsByUserIdAndProductId(userId, productId)) {
            productLikeRepository.deleteByUserIdAndProductId(userId, productId);
            product.setLikeCount(Math.max(0, product.getLikeCount() - 1));
            productRepository.save(product);
        }
    }

    @Transactional
    public void favoriteProduct(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (!productFavoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            ProductFavorite favorite = new ProductFavorite();
            favorite.setProduct(product);
            User user = new User();
            user.setId(userId);
            favorite.setUser(user);
            productFavoriteRepository.save(favorite);

            product.setFavoriteCount(product.getFavoriteCount() + 1);
            productRepository.save(product);
        }
    }

    @Transactional
    public void unfavoriteProduct(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (productFavoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            productFavoriteRepository.deleteByUserIdAndProductId(userId, productId);
            product.setFavoriteCount(Math.max(0, product.getFavoriteCount() - 1));
            productRepository.save(product);
        }
    }
    
    @Transactional(readOnly = true)
    public Page<Product> getUserFavorites(Long userId, Pageable pageable) {
        Page<ProductFavorite> favorites = productFavoriteRepository.findByUserId(userId, pageable);
        return favorites.map(ProductFavorite::getProduct);
    }
    
    @Transactional(readOnly = true)
    public boolean isProductFavorited(Long productId, Long userId) {
        return productFavoriteRepository.existsByUserIdAndProductId(userId, productId);
    }
} 
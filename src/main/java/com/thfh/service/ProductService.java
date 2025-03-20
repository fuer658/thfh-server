package com.thfh.service;

import com.thfh.dto.ProductDTO;
import com.thfh.dto.ProductSearchDTO;
import com.thfh.model.Product;
import com.thfh.model.ProductStatus;
import com.thfh.repository.ProductRepository;
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
} 
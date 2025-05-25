package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 作品图册实体类
 * 用于存储作品的扩展图片
 */
@Data
@Entity
@Table(name = "artwork_gallery")
@Schema(description = "作品图册 - 存储作品的扩展图片信息")
public class ArtworkGallery {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "图册ID", example = "1")
    private Long id;
    
    /**
     * 关联的作品
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id", nullable = false)
    @Schema(description = "关联的作品", required = true)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Artwork artwork;
    
    /**
     * 图片URL
     */
    @Column(nullable = false, length = 500)
    @Schema(description = "图片URL", example = "http://example.com/image.jpg", required = true)
    private String imageUrl;
    
    /**
     * 图片描述
     */
    @Column(length = 500)
    @Schema(description = "图片描述", example = "作品细节展示")
    private String description;
    
    /**
     * 排序序号
     */
    @Column(nullable = false)
    @Schema(description = "排序序号", example = "1", required = true)
    private Integer sortOrder;
    
    /**
     * 创建时间
     */
    @Column(nullable = false)
    @Schema(description = "创建时间", example = "2024-01-01 12:00:00", required = true)
    private LocalDateTime createTime = LocalDateTime.now();
    
    /**
     * 更新时间
     */
    @Column(nullable = false)
    @Schema(description = "更新时间", example = "2024-01-01 12:00:00", required = true)
    private LocalDateTime updateTime = LocalDateTime.now();
} 
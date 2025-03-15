package com.thfh.dto;

import com.thfh.model.ArtworkType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ArtworkDTO {
    private Long id;
    private String title;
    private String description;
    private String coverUrl;
    private String materials;
    private BigDecimal price;
    private ArtworkType type;
    
    // 创建者信息
    private Long creatorId;
    private String creatorName;
    private String creatorAvatar;
    
    private Set<TagDTO> tags;
    private Boolean recommended;
    private Boolean enabled;
    private BigDecimal averageScore;
    private Integer scoreCount;
    private Integer favoriteCount;
    private Integer likeCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 
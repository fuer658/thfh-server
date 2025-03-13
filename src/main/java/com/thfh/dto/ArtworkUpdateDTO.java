package com.thfh.dto;

import com.thfh.model.ArtworkTag;
import com.thfh.model.ArtworkType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

/**
 * 作品更新DTO
 * 用于接收管理员编辑作品的请求
 */
@Data
public class ArtworkUpdateDTO {
    
    @NotBlank(message = "作品标题不能为空")
    private String title;
    
    private String description;
    
    private String coverUrl;
    
    private String materials;
    
    private BigDecimal price;
    
    @NotNull(message = "作品类型不能为空")
    private ArtworkType type;
    
    private Set<ArtworkTag> tags;
    
    private Boolean recommended;
    
    private Boolean enabled;
} 
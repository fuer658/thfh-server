package com.thfh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "artworks")
@ApiModel(value = "作品", description = "艺术作品信息")
public class Artwork {
    @ApiModelProperty(value = "作品ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "作品标题", required = true, example = "春天的色彩")
    @Column(nullable = false)
    private String title;

    @ApiModelProperty(value = "作品描述", example = "这是一幅描绘春天景色的油画作品")
    @Column(length = 2000)
    private String description;

    @ApiModelProperty(value = "封面图片URL", example = "http://example.com/images/artwork.jpg")
    private String coverUrl;
    
    @ApiModelProperty(value = "创作材料", example = "油画颜料、画布")
    @Column(length = 2000)
    private String materials;

    @ApiModelProperty(value = "作品价格", example = "1999.99")
    private BigDecimal price;

    @ApiModelProperty(value = "作品类型", required = true, example = "PERSONAL")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArtworkType type;

    @ApiModelProperty(value = "创作者信息")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "email", "phone", "createTime", "updateTime", "lastLoginTime", "status", "roles","qualification","speciality","disability","points","birthday"})
    private User creator;

    @ApiModelProperty(value = "作品标签")
    @ManyToMany
    @JoinTable(
        name = "artwork_tag_relation",
        joinColumns = @JoinColumn(name = "artwork_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<ArtworkTag> tags = new HashSet<>();

    @ApiModelProperty(value = "是否推荐", example = "false")
    private Boolean recommended = false;

    @ApiModelProperty(value = "是否启用", example = "true")
    private Boolean enabled = true;

    @ApiModelProperty(value = "平均评分", example = "4.5")
    private BigDecimal averageScore = BigDecimal.ZERO;

    @ApiModelProperty(value = "评分次数", example = "10")
    private Integer scoreCount = 0;

    @ApiModelProperty(value = "总评分", example = "45.0")
    @Column(precision = 3, scale = 2)
    private BigDecimal totalScore = BigDecimal.ZERO;

    @ApiModelProperty(value = "收藏数量", example = "20")
    private Integer favoriteCount = 0;

    @ApiModelProperty(value = "点赞数量", example = "50")
    private Integer likeCount = 0;

    @ApiModelProperty(value = "浏览次数", example = "100")
    private Integer viewCount = 0;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
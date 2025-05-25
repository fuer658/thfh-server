package com.thfh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "post_tags")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Schema(description = "PostTag - 帖子标签实体")
public class PostTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "标签ID", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "标签名称", example = "教学技巧", required = true)
    private String name;

    @Column(length = 500)
    @Schema(description = "标签描述", example = "关于教学技巧的讨论和分享")
    private String description;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled = true;

    @Schema(description = "热度（所有关联动态的浏览量之和）", example = "1000")
    @Transient // 不持久化，动态计算
    private Long hotness = 0L;

    // 默认构造函数
    public PostTag() {}

    // 接收标签名称的构造函数
    public PostTag(String name) {
        this.name = name;
        this.enabled = true;
    }

    public Long getHotness() {
        return hotness;
    }

    public void setHotness(Long hotness) {
        this.hotness = hotness;
    }
} 
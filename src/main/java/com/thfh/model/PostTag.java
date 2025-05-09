package com.thfh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "post_tags")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ApiModel(value = "PostTag", description = "帖子标签实体")
public class PostTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "标签ID", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @ApiModelProperty(value = "标签名称", example = "教学技巧", required = true)
    private String name;

    @Column(length = 500)
    @ApiModelProperty(value = "标签描述", example = "关于教学技巧的讨论和分享")
    private String description;

    @ApiModelProperty(value = "是否启用", example = "true")
    private Boolean enabled = true;

    // 默认构造函数
    public PostTag() {}

    // 接收标签名称的构造函数
    public PostTag(String name) {
        this.name = name;
        this.enabled = true;
    }
} 
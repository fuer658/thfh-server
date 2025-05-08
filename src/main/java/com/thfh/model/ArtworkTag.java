package com.thfh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "artwork_tags")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ApiModel(value = "作品标签", description = "用于对作品进行分类和标记的标签")
public class ArtworkTag {
    @ApiModelProperty(value = "标签ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "标签名称", required = true, example = "油画")
    @Column(nullable = false, unique = true)
    private String name;

    @ApiModelProperty(value = "标签描述", example = "使用油彩颜料在画布上创作的艺术作品")
    @Column(length = 500)
    private String description;

    @ApiModelProperty(value = "是否启用", example = "true")
    private Boolean enabled = true;

    // 默认构造函数
    public ArtworkTag() {}

    // 接收标签名称的构造函数
    public ArtworkTag(String name) {
        this.name = name;
        this.enabled = true;
    }
}
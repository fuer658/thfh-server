package com.thfh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "post_tags")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PostTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    private Boolean enabled = true;

    // 默认构造函数
    public PostTag() {}

    // 接收标签名称的构造函数
    public PostTag(String name) {
        this.name = name;
        this.enabled = true;
    }
} 
package com.thfh.model;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "artwork_tags")
public class ArtworkTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    private Boolean enabled = true;

    // 默认构造函数
    public ArtworkTag() {}

    // 接收标签名称的构造函数
    public ArtworkTag(String name) {
        this.name = name;
        this.enabled = true;
    }
}
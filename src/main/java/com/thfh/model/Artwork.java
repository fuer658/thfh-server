package com.thfh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "artworks")
public class Artwork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String coverUrl;
    
    @Column(length = 2000)
    private String materials;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArtworkType type;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "email", "phone", "createTime", "updateTime", "lastLoginTime", "status", "roles","qualification","speciality","disability","points","birthday"})
    private User creator;

    @ManyToMany
    @JoinTable(
        name = "artwork_tag_relation",
        joinColumns = @JoinColumn(name = "artwork_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<ArtworkTag> tags = new HashSet<>();

    private Boolean recommended = false;

    private Boolean enabled = true;

    private BigDecimal averageScore = BigDecimal.ZERO;

    private Integer scoreCount = 0;

    @Column(precision = 3, scale = 2)
    private BigDecimal totalScore = BigDecimal.ZERO;

    private Integer favoriteCount = 0;

    private Integer likeCount = 0;

    private LocalDateTime createTime;

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
package com.thfh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "post_comments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "email", "phone", "createTime", "updateTime", "lastLoginTime", "status", "roles","qualification","speciality","disability","points","birthday"})
    private User user;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "comments", "title", "content", "imageUrls", "userId", "user", "likeCount", "commentCount", "shareCount", "createTime", "updateTime", "tags"})
    private Post post;

    @Column(name = "parent_id")
    private Long parentId;  // 父评论ID，如果是一级评论则为null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "parent", "children", "post", "user"})
    private PostComment parent;  // 父评论对象

    @OneToMany(mappedBy = "parent")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "parent", "post", "user"})
    private List<PostComment> children = new ArrayList<>();  // 子评论列表

    @Column(name = "level")
    private Integer level = 1;  // 评论层级，1为一级评论，2为二级评论，以此类推

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
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
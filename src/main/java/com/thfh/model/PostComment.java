package com.thfh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "post_comments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ApiModel(value = "帖子评论", description = "用户对帖子发表的评论")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "评论ID", example = "1")
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    @ApiModelProperty(value = "评论内容", required = true, example = "这个作品非常精美！")
    private String content;

    @Column(name = "user_id", nullable = false)
    @ApiModelProperty(value = "评论用户ID", required = true, example = "1")
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "email", "phone", "createTime", "updateTime", "lastLoginTime", "status", "roles","disability","points","birthday"})
    @ApiModelProperty(value = "评论用户", notes = "发表评论的用户")
    private User user;

    @Column(name = "post_id", nullable = false)
    @ApiModelProperty(value = "帖子ID", required = true, example = "1")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "comments", "title", "content", "imageUrls", "userId", "user", "likeCount", "commentCount", "shareCount", "createTime", "updateTime", "tags"})
    @ApiModelProperty(value = "所属帖子", notes = "评论所属的帖子")
    private Post post;

    @Column(name = "parent_id")
    @ApiModelProperty(value = "父评论ID", example = "1", notes = "如果是一级评论则为null")
    private Long parentId;  // 父评论ID，如果是一级评论则为null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "parent", "children", "post", "user"})
    @ApiModelProperty(value = "父评论", notes = "评论的父级评论对象")
    private PostComment parent;  // 父评论对象

    @OneToMany(mappedBy = "parent")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "parent", "post", "user"})
    @ApiModelProperty(value = "子评论列表", notes = "评论下的回复评论列表")
    private List<PostComment> children = new ArrayList<>();  // 子评论列表

    @Column(name = "level")
    @ApiModelProperty(value = "评论层级", example = "1", notes = "1为一级评论，2为二级评论，以此类推")
    private Integer level = 1;  // 评论层级，1为一级评论，2为二级评论，以此类推

    @Column(name = "like_count")
    @ApiModelProperty(value = "点赞数量", example = "10")
    private Integer likeCount = 0;

    @Column(name = "create_time", nullable = false)
    @ApiModelProperty(value = "创建时间", example = "2023-01-01T12:00:00")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间", example = "2023-01-02T12:00:00")
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
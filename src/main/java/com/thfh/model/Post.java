package com.thfh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "posts")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ApiModel(value = "帖子", description = "用户发布的社区帖子")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "帖子ID", example = "1")
    private Long id;

    @Column(name = "user_id", nullable = false)
    @ApiModelProperty(value = "发布用户ID", required = true, example = "1")
    private Long userId;

    @Column(nullable = false)
    @ApiModelProperty(value = "帖子标题", required = true, example = "分享我的汉服刺绣作品")
    private String title;

    @Column(columnDefinition = "TEXT")
    @ApiModelProperty(value = "帖子内容", example = "今天完成了一件汉服的刺绣...")
    private String content;

    @ElementCollection
    @CollectionTable(name = "post_image_urls", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_url")
    @ApiModelProperty(value = "图片URL列表", example = "[\"http://example.com/image1.jpg\", \"http://example.com/image2.jpg\"]")
    private List<String> imageUrls = new ArrayList<>();

    @Column(name = "like_count")
    @ApiModelProperty(value = "点赞数量", example = "42")
    private Integer likeCount = 0;

    @Column(name = "comment_count")
    @ApiModelProperty(value = "评论数量", example = "15")
    private Integer commentCount = 0;

    @Column(name = "share_count")
    @ApiModelProperty(value = "分享数量", example = "5")
    private Integer shareCount = 0;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false)
    @ApiModelProperty(value = "创建时间", example = "2023-01-01T12:00:00")
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间", example = "2023-01-02T12:00:00")
    private LocalDateTime updateTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "post_tags_relation",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ApiModelProperty(value = "帖子标签", notes = "帖子关联的标签集合")
    private Set<PostTag> tags = new HashSet<>();

    @Transient
    @ApiModelProperty(value = "标签ID集合", hidden = true)
    private Set<Long> tagIds = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "email", "phone", "createTime", "updateTime", "lastLoginTime", "status", "roles","qualification","speciality","disability","points","birthday"})
    @ApiModelProperty(value = "发布用户", notes = "帖子的发布者")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("post")
    @ApiModelProperty(value = "评论列表", notes = "帖子下的所有评论")
    private List<PostComment> comments;
}
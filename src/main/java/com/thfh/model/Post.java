package com.thfh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 帖子实体
 */
@Data
@Getter
@Setter
@Entity
@Table(name = "post")
@Schema(description = "帖子实体 - 社区中的帖子内容")
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "帖子ID", example = "1")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "发帖用户")
    private User user;
    
    @Column(nullable = false)
    @Schema(description = "帖子标题", example = "分享一个学习经验")
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    @Schema(description = "帖子内容", example = "这是帖子的详细内容...")
    private String content;
    
    @Schema(description = "帖子类型", example = "DISCUSSION")
    private String type;
    
    @Schema(description = "帖子状态", example = "PUBLISHED")
    private String status;
    
    @Schema(description = "浏览次数", example = "100")
    private Integer viewCount;
    
    @Schema(description = "点赞次数", example = "50")
    private Integer likeCount;
    
    @Schema(description = "收藏次数", example = "20")
    private Integer favoriteCount;
    
    @Schema(description = "评论次数", example = "30")
    private Integer commentCount;
    
    @Schema(description = "是否置顶", example = "false")
    private Boolean isTop;
    
    @Schema(description = "是否精华", example = "false")
    private Boolean isEssence;
    
    @Schema(description = "是否允许评论", example = "true")
    private Boolean allowComment;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @Schema(description = "最后回复时间")
    private LocalDateTime lastReplyTime;
    
    @Schema(description = "封面图片 - 帖子的封面图片URL", example = "https://example.com/cover.jpg")
    private String coverImage;
    
    @Schema(description = "标签", example = "学习,经验,分享")
    private String tags;
    
    @Schema(description = "摘要", example = "这是一篇关于学习经验的分享...")
    private String summary;
    
    @Schema(description = "来源", example = "原创")
    private String source;
    
    @Schema(description = "原文链接", example = "https://example.com/original")
    private String originalUrl;
    
    @Schema(description = "附件URL", example = "https://example.com/attachment.pdf")
    private String attachmentUrl;
    
    @Schema(description = "地理位置", example = "北京市海淀区")
    private String location;
    
    @Schema(description = "IP地址", example = "192.168.1.1")
    private String ipAddress;
    
    @Schema(description = "设备信息", example = "iPhone 12")
    private String deviceInfo;
    
    @Schema(description = "是否匿名", example = "false")
    private Boolean isAnonymous;
    
    @Schema(description = "是否原创", example = "true")
    private Boolean isOriginal;
    
    @Schema(description = "是否有附件", example = "false")
    private Boolean hasAttachment;
    
    @Schema(description = "是否有图片", example = "true")
    private Boolean hasImage;
    
    @Schema(description = "是否有视频", example = "false")
    private Boolean hasVideo;
    
    @Schema(description = "图片URL列表", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
    private String imageUrls;
    
    @Schema(description = "视频URL", example = "https://example.com/video.mp4")
    private String videoUrl;

    @ManyToMany
    @JoinTable(
        name = "post_tags",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Schema(description = "动态标签集合")
    private Set<PostTag> tags = new HashSet<>();

    @Transient
    @Schema(description = "标签名称列表")
    private List<String> tagNames;

    @Transient 
    @Schema(description = "标签ID列表")
    private List<Long> tagIds;

    public List<String> getTagNames() {
        return tags.stream()
                .map(PostTag::getName)
                .collect(Collectors.toList());
    }

    public List<Long> getTagIds() {
        return tags.stream()
                .map(PostTag::getId)
                .collect(Collectors.toList());
    }
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("post")
    private List<Comment> comments = new ArrayList<>();
    
    @PrePersist
    public void prePersist() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        if (updateTime == null) {
            updateTime = LocalDateTime.now();
        }
        if (lastReplyTime == null) {
            lastReplyTime = createTime;
        }
        if (viewCount == null) {
            viewCount = 0;
        }
        if (likeCount == null) {
            likeCount = 0;
        }
        if (favoriteCount == null) {
            favoriteCount = 0;
        }
        if (commentCount == null) {
            commentCount = 0;
        }
        if (isTop == null) {
            isTop = false;
        }
        if (isEssence == null) {
            isEssence = false;
        }
        if (allowComment == null) {
            allowComment = true;
        }
        if (isAnonymous == null) {
            isAnonymous = false;
        }
        if (isOriginal == null) {
            isOriginal = true;
        }
        if (hasAttachment == null) {
            hasAttachment = false;
        }
        if (hasImage == null) {
            hasImage = false;
        }
        if (hasVideo == null) {
            hasVideo = false;
        }
        if (status == null) {
            status = "PUBLISHED";
        }
        if (type == null) {
            type = "DISCUSSION";
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}

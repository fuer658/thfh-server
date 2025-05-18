package com.thfh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "post_browse_history", 
       uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "post_id"})})
@ApiModel(value = "动态浏览记录", description = "记录用户浏览动态的历史")
public class PostBrowseHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "记录ID", example = "1")
    private Long id;

    @Column(name = "user_id", nullable = false)
    @ApiModelProperty(value = "用户ID", required = true, example = "1")
    private Long userId;

    @Column(name = "post_id", nullable = false)
    @ApiModelProperty(value = "动态ID", required = true, example = "1")
    private Long postId;

    @CreationTimestamp
    @Column(name = "browse_time", nullable = false)
    @ApiModelProperty(value = "首次浏览时间", example = "2023-05-01T12:00:00")
    private LocalDateTime browseTime;

    @Column(name = "last_browse_time")
    @ApiModelProperty(value = "最后浏览时间", example = "2023-05-02T14:30:00")
    private LocalDateTime lastBrowseTime;

    @Column(name = "browse_count", nullable = false)
    @ApiModelProperty(value = "浏览次数", example = "5")
    private Integer browseCount = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "email", "phone", "createTime", "updateTime", "lastLoginTime", "status", "roles", "disability", "points", "birthday"})
    @ApiModelProperty(value = "用户信息", hidden = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "content", "imageUrls", "tags", "tagIds", "tagNames", "comments"})
    @ApiModelProperty(value = "动态信息", hidden = true)
    private Post post;
} 
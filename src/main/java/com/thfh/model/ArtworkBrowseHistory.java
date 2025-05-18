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
@Table(name = "artwork_browse_history", 
       uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "artwork_id"})})
@ApiModel(value = "作品浏览记录", description = "记录用户浏览作品的历史")
public class ArtworkBrowseHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "记录ID", example = "1")
    private Long id;

    @Column(name = "user_id", nullable = false)
    @ApiModelProperty(value = "用户ID", required = true, example = "1")
    private Long userId;

    @Column(name = "artwork_id", nullable = false)
    @ApiModelProperty(value = "作品ID", required = true, example = "1")
    private Long artworkId;

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
    @JoinColumn(name = "artwork_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ApiModelProperty(value = "作品信息", hidden = true)
    private Artwork artwork;

    @PrePersist
    protected void onCreate() {
        browseTime = LocalDateTime.now();
        lastBrowseTime = LocalDateTime.now();
    }
} 
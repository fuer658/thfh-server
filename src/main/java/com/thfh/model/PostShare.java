package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "post_share", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "post_id"})
})
@ApiModel(value = "PostShare", description = "帖子分享记录实体")
public class PostShare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "分享记录ID", example = "1")
    private Long id;

    @Column(name = "user_id", nullable = false)
    @ApiModelProperty(value = "用户ID", example = "1001", required = true)
    private Long userId;

    @Column(name = "post_id", nullable = false)
    @ApiModelProperty(value = "帖子ID", example = "2001", required = true)
    private Long postId;

    @Column(name = "create_time", nullable = false)
    @ApiModelProperty(value = "创建时间", example = "2023-01-01T12:00:00")
    private LocalDateTime createTime = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    @ApiModelProperty(value = "关联的帖子对象")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @ApiModelProperty(value = "关联的用户对象")
    private User user;
}
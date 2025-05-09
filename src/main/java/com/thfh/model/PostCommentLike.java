package com.thfh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "post_comment_like", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "comment_id"})
})
@ApiModel(value = "评论点赞", description = "用户对评论的点赞记录")
public class PostCommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "点赞ID", example = "1")
    private Long id;

    @Column(name = "user_id", nullable = false)
    @ApiModelProperty(value = "用户ID", required = true, example = "1")
    private Long userId;

    @Column(name = "comment_id", nullable = false)
    @ApiModelProperty(value = "评论ID", required = true, example = "1")
    private Long commentId;

    @Column(name = "create_time", nullable = false)
    @ApiModelProperty(value = "创建时间", example = "2023-01-01T12:00:00")
    private LocalDateTime createTime = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    @ApiModelProperty(value = "评论对象", notes = "点赞的评论")
    private PostComment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "email", "phone", "createTime", "updateTime", "lastLoginTime", "status", "roles","qualification","speciality","disability","points","birthday"})
    @ApiModelProperty(value = "用户对象", notes = "点赞的用户")
    private User user;
}

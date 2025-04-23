package com.thfh.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 帖子评论DTO
 * 用于前端展示评论信息，避免实体类懒加载问题
 */
@Data
public class PostCommentDTO {
    private Long id;
    private String content;
    private Long userId;
    private String userName;
    private String userRealName;
    private String userAvatar;
    private Long postId;
    private Long parentId;
    private Integer level;
    private Integer likeCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<PostCommentDTO> children = new ArrayList<>();
}

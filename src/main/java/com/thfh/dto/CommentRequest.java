package com.thfh.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;  // 评论内容
    private Long parentId;  // 父评论ID，如果是一级评论则为null
} 
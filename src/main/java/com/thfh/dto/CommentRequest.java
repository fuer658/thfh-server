package com.thfh.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CommentRequest {
    @NotBlank(message = "评论内容不能为空")
    private String content;  // 评论内容
    private Long parentId;  // 父评论ID，如果是一级评论则为null
} 
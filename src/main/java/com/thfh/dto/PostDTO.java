package com.thfh.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private List<String> imageUrls;
    private Long userId;
    private String userName;
    private String userRealName;
    private String userAvatar;
    private Integer likeCount;
    private Integer commentCount;
    private Integer shareCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 
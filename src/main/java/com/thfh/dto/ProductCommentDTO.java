package com.thfh.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel("商品评论DTO")
public class ProductCommentDTO {
    @ApiModelProperty("评论ID")
    private Long id;

    @ApiModelProperty("商品ID")
    private Long productId;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("用户昵称")
    private String userNickname;

    @ApiModelProperty("用户头像URL")
    private String userAvatar;

    @ApiModelProperty(value = "评论内容", required = true)
    @javax.validation.constraints.NotBlank(message = "评论内容不能为空")
    @javax.validation.constraints.Size(max = 500, message = "评论内容不能超过500字")
    private String content;

    @ApiModelProperty("父评论ID（如为根评论可不传或为null）")
    private Long parentId;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("二级评论列表，仅一级评论包含该字段，最多只返回一层回复")
    private List<ProductCommentDTO> replies;

    @ApiModelProperty(value = "评论图片URL列表，最多9张")
    private List<String> images;

    @ApiModelProperty(value = "追评内容，仅一级评论包含该字段")
    private ProductCommentDTO appendComment;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Long getId() {
        return id;
    }

    public void setReplies(List<ProductCommentDTO> replies) {
        this.replies = replies;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ProductCommentDTO getAppendComment() {
        return appendComment;
    }

    public void setAppendComment(ProductCommentDTO appendComment) {
        this.appendComment = appendComment;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // getter/setter 省略，可用lombok
} 
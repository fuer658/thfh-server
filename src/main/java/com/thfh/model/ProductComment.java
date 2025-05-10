package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel("商品评论实体")
@Entity
@Table(name = "product_comment")
public class ProductComment {
    @ApiModelProperty("评论ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty("商品ID")
    @Column(nullable = false)
    private Long productId;

    @ApiModelProperty("用户ID")
    @Column(nullable = false)
    private Long userId;

    @ApiModelProperty("评论内容")
    @Column(nullable = false, length = 500)
    private String content;

    @ApiModelProperty("父评论ID")
    private Long parentId;

    @ApiModelProperty("创建时间")
    @Column(nullable = false)
    private LocalDateTime createTime;

    @ApiModelProperty("点赞数")
    @Column(nullable = false)
    private Integer likeCount = 0;

    @ApiModelProperty(value = "评论图片URL列表，最多9张")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_comment_images", joinColumns = @JoinColumn(name = "comment_id"))
    @Column(name = "image_url", length = 512)
    private List<String> images;

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    // getter/setter 省略，可用lombok
} 
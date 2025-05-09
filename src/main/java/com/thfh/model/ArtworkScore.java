package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "artwork_scores")
@ApiModel(value = "作品评分", description = "用户对作品的评分信息")
public class ArtworkScore {
    @ApiModelProperty(value = "评分ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "作品信息")
    @ManyToOne
    @JoinColumn(name = "artwork_id", nullable = false)
    private Artwork artwork;

    @ApiModelProperty(value = "评分用户")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ApiModelProperty(value = "评分分数", required = true, example = "85.5", notes = "评分范围0-100，支持两位小数")
    @Column(nullable = false, precision = 5, scale = 2)
    @Min(value = 0, message = "评分不能小于0")
    @Max(value = 100, message = "评分不能大于100")
    @Digits(integer = 3, fraction = 2, message = "评分最多支持3位整数和2位小数")
    private BigDecimal score;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
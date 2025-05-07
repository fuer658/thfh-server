package com.thfh.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

/**
 * 作品评分数据传输对象
 */
@ApiModel(value = "作品评分信息", description = "用于提交作品评分")
public class ArtworkScoreDTO {
    @Min(value = 0, message = "评分不能小于0")
    @Max(value = 100, message = "评分不能大于100")
    @Digits(integer = 3, fraction = 2, message = "评分最多支持3位整数和2位小数")
    @ApiModelProperty(value = "评分", required = true, notes = "0-100之间，支持小数", example = "85.5")
    private BigDecimal score;

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }
}
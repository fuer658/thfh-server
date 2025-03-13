package com.thfh.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

public class ArtworkScoreDTO {
    @Min(value = 0, message = "评分不能小于0")
    @Max(value = 100, message = "评分不能大于100")
    @Digits(integer = 3, fraction = 2, message = "评分最多支持3位整数和2位小数")
    private BigDecimal score;

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }
}
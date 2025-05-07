package com.thfh.dto;

import com.thfh.model.ProductStatus;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品数据传输对象
 * 用于前后端数据交互
 */
@Data
@ApiModel(value = "商品信息", description = "包含商品的详细信息")
public class ProductDTO {
    /**
     * 商品ID
     */
    @ApiModelProperty(value = "商品ID", notes = "唯一标识", example = "1")
    private Long id;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称", required = true, example = "残障人士辅助工具")
    private String name;

    /**
     * 商品描述
     */
    @ApiModelProperty(value = "商品描述", example = "一款专为残障人士设计的辅助工具")
    private String description;

    /**
     * 商品价格
     */
    @ApiModelProperty(value = "商品价格", example = "199.99")
    private BigDecimal price;

    /**
     * 商品库存数量
     */
    @ApiModelProperty(value = "库存数量", example = "100")
    private Integer stock;

    /**
     * 商品图片URL列表
     * 支持多张图片
     */
    @ApiModelProperty(value = "图片URL列表", notes = "支持多张图片", example = "[\"https://example.com/img1.jpg\", \"https://example.com/img2.jpg\"]")
    private List<String> imageUrls;

    /**
     * 商品状态
     */
    @ApiModelProperty(value = "商品状态", example = "ON_SALE")
    private ProductStatus status;

    /**
     * 搜索关键词
     */
    @ApiModelProperty(value = "搜索关键词", example = "辅助,工具,残障")
    private String keywords;

    /**
     * 商品分类
     */
    @ApiModelProperty(value = "商品分类", example = "辅助工具")
    private String category;
    
    /**
     * 商品点赞数
     */
    @ApiModelProperty(value = "点赞数", example = "156")
    private Integer likeCount = 0;
    
    /**
     * 商品收藏数
     */
    @ApiModelProperty(value = "收藏数", example = "89")
    private Integer favoriteCount = 0;
} 
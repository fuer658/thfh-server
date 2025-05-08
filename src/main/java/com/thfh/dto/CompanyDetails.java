package com.thfh.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 公司详细信息DTO
 * 用于企业用户注册时传递公司信息
 */
@Data
@ApiModel(value = "公司详细信息", description = "企业用户注册时用于传递公司详细信息")
public class CompanyDetails {

    /**
     * 公司名称
     */
    @ApiModelProperty(value = "公司名称", example = "北京XX科技有限公司")
    private String name;

    /**
     * 公司行业
     */
    @ApiModelProperty(value = "公司行业", example = "互联网/IT/计算机")
    private String industry;

    /**
     * 公司地址
     */
    @ApiModelProperty(value = "公司地址", example = "北京市海淀区中关村大街1号")
    private String address;

    /**
     * 公司网站（选填）
     */
    @ApiModelProperty(value = "公司网站", notes = "选填项", example = "https://www.example.com")
    private String website;
} 
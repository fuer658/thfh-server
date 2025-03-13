package com.thfh.dto;

import javax.validation.constraints.NotBlank;

public class TagDTO {
    @NotBlank(message = "标签名称不能为空")
    private String tagName;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
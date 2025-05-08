package com.thfh.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

/**
 * 字符串列表转换器
 * 用于将List<String>与数据库中的JSON字符串相互转换
 */
@Converter
@ApiModel(value = "StringListConverter", description = "字符串列表转换器，用于List<String>与JSON字符串互转")
public class StringListConverter implements AttributeConverter<List<String>, String> {
    @ApiModelProperty(value = "Jackson对象映射器", hidden = true)
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将Java中的字符串列表转换为数据库中存储的JSON字符串
     * @param attribute 待转换的字符串列表
     * @return JSON格式的字符串
     */
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("无法将List<String>转换为JSON字符串: " + e.getMessage(), e);
        }
    }

    /**
     * 将数据库中的JSON字符串转换为Java中的字符串列表
     * @param dbData 数据库中的JSON字符串
     * @return 字符串列表
     */
    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            String cleanedData = dbData.trim();
            if (!cleanedData.startsWith("[") || !cleanedData.endsWith("]")) {
                // 如果不是标准的JSON数组格式，尝试将其作为单个字符串处理
                List<String> result = new ArrayList<>();
                result.add(cleanedData);
                return result;
            }
            return objectMapper.readValue(cleanedData, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            // 转换失败时返回空列表，避免异常传播
            return new ArrayList<>();
        }
    }
}
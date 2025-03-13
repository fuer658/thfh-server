package com.thfh.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

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
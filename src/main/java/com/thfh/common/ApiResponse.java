package com.thfh.common;

import lombok.Data;

/**
 * API响应类，用于统一接口返回格式
 * @param <T> 返回数据类型
 */
@Data
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T data;

    private ApiResponse() {
    }

    /**
     * 成功响应
     * 
     * @param data 返回数据
     * @return 响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }
    
    /**
     * 错误响应
     * 
     * @param code 错误码
     * @param message 错误信息
     * @return 响应对象
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
    
    /**
     * 通用错误响应
     * 
     * @param message 错误信息
     * @return 响应对象
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(500, message);
    }
} 
package com.thfh.common;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@ApiModel(value = "统一响应结果", description = "所有API的统一返回格式")
public class Result<T> {
    @ApiModelProperty(value = "状态码", notes = "200-成功，401-未授权，500-错误", example = "200")
    private Integer code;
    
    @ApiModelProperty(value = "响应消息", example = "操作成功")
    private String message;
    
    @ApiModelProperty(value = "响应数据")
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }
    
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }
    
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> unauthorized(String message) {
        Result<T> result = new Result<>();
        result.setCode(401);
        result.setMessage(message);
        return result;
    }
} 
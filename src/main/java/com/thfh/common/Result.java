package com.thfh.common;

import com.thfh.exception.ErrorCode;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.http.HttpStatus;

@Data
@ApiModel(value = "统一响应结果", description = "所有API的统一返回格式")
public class Result<T> {
    
    public static final int SUCCESS = HttpStatus.OK.value();
    public static final int FAILURE = HttpStatus.INTERNAL_SERVER_ERROR.value();
    public static final int UNAUTHORIZED = HttpStatus.UNAUTHORIZED.value();
    public static final int BAD_REQUEST = HttpStatus.BAD_REQUEST.value();
    public static final int NOT_FOUND = HttpStatus.NOT_FOUND.value();
    public static final int FORBIDDEN = HttpStatus.FORBIDDEN.value();
    
    @ApiModelProperty(value = "状态码", notes = "200-成功，401-未授权，500-错误等", example = "200")
    private Integer code;
    
    @ApiModelProperty(value = "响应消息", example = "操作成功")
    private String message;
    
    @ApiModelProperty(value = "响应数据")
    private T data;
    
    @ApiModelProperty(value = "时间戳", notes = "请求处理的时间戳", example = "1624007689420")
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS);
        result.setMessage("操作成功");
        return result;
    }
    
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }
    
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
    
    public static <T> Result<T> failure() {
        return failure("操作失败");
    }

    public static <T> Result<T> failure(String message) {
        Result<T> result = new Result<>();
        result.setCode(FAILURE);
        result.setMessage(message);
        return result;
    }
    
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(FAILURE);
        result.setMessage(message);
        return result;
    }
    
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    
    public static <T> Result<T> error(ErrorCode errorCode) {
        Result<T> result = new Result<>();
        result.setCode(errorCode.getCode());
        result.setMessage(errorCode.getMessage());
        return result;
    }
    
    public static <T> Result<T> error(ErrorCode errorCode, String detail) {
        Result<T> result = new Result<>();
        result.setCode(errorCode.getCode());
        result.setMessage(errorCode.getMessage() + ": " + detail);
        return result;
    }

    public static <T> Result<T> unauthorized(String message) {
        Result<T> result = new Result<>();
        result.setCode(UNAUTHORIZED);
        result.setMessage(message);
        return result;
    }
    
    public static <T> Result<T> badRequest(String message) {
        Result<T> result = new Result<>();
        result.setCode(BAD_REQUEST);
        result.setMessage(message);
        return result;
    }
    
    public static <T> Result<T> notFound(String message) {
        Result<T> result = new Result<>();
        result.setCode(NOT_FOUND);
        result.setMessage(message);
        return result;
    }
    
    public static <T> Result<T> forbidden(String message) {
        Result<T> result = new Result<>();
        result.setCode(FORBIDDEN);
        result.setMessage(message);
        return result;
    }
    
    public boolean isSuccess() {
        return SUCCESS == this.code;
    }
} 
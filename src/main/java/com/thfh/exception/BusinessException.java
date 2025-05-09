package com.thfh.exception;

import lombok.Getter;

/**
 * 业务异常
 * 用于表示业务处理过程中的错误情况
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private ErrorCode errorCode;

    /**
     * 构造函数
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = ErrorCode.BUSINESS_ERROR;
    }

    /**
     * 构造函数
     * @param message 错误消息
     * @param cause 原始异常
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.BUSINESS_ERROR;
    }
    
    /**
     * 构造函数
     * @param errorCode 错误码
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    /**
     * 构造函数
     * @param errorCode 错误码
     * @param cause 原始异常
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
    
    /**
     * 构造函数
     * @param errorCode 错误码
     * @param detail 详细错误信息
     */
    public BusinessException(ErrorCode errorCode, String detail) {
        super(errorCode.getMessage() + ": " + detail);
        this.errorCode = errorCode;
    }
} 
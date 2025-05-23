package com.thfh.exception;

/**
 * 请求参数错误或业务逻辑错误的异常
 */
public class BadRequestException extends RuntimeException {

    /**
     * 创建一个带有错误信息的BadRequestException
     * @param message 错误信息
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * 创建一个带有错误信息和原因的BadRequestException
     * @param message 错误信息
     * @param cause 原因
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
} 
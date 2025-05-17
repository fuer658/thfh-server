package com.thfh.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 用户未登录异常
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotLoggedInException extends RuntimeException {
    public UserNotLoggedInException(String message) {
        super(message);
    }

    public UserNotLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }
} 